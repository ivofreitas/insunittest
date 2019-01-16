package br.com.api.gurgel.service.impl;

import br.com.api.gurgel.model.Token;
import br.com.api.gurgel.model.NotificationRequest;
import br.com.api.gurgel.model.NotificationResponse;
import br.com.api.gurgel.repository.TokenRepository;
import br.com.api.gurgel.repository.NotificationRepository;
import br.com.api.gurgel.service.NotificationService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Value("${sns.senderId}")
    private String senderId;

    private String arnStorage;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private TokenRepository tokenRepository;
    
    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private AmazonSNS amazonSNS;
    
    @Override
    public Mono<NotificationResponse> newTopicNotification(NotificationRequest notificationRequest) throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException {

        CreatePlatformApplicationResult platformApplicationResult = createPlatformApp("InspectorApp", notificationRequest.getPlatform(), "");

        CreateTopicRequest createTopicRequest = new CreateTopicRequest()
                .withName(notificationRequest.getTopicName());
        CreateTopicResult createTopicResult = amazonSNS.createTopic(createTopicRequest);

        CompletableFuture<List<Token>> tokenCompletableFuture = tokenRepository.findAll().collect(Collectors.toList()).toFuture();

        if (tokenCompletableFuture.get(5, TimeUnit.SECONDS) == null) {
            throw new IllegalArgumentException("Invalid client code");
        }

        List<String> tokens = tokenCompletableFuture.get().stream().map(token -> token.getTokenKey()).collect(Collectors.toList());

        tokens.stream().forEach(token -> {
            registerWithSNS(token, platformApplicationResult.getPlatformApplicationArn());

            SubscribeRequest subscribeRequest = new SubscribeRequest()
                    .withTopicArn(createTopicResult.getTopicArn())
                    .withProtocol("application")
                    .withEndpoint(arnStorage);

            storeEndpointArn(null);

            amazonSNS.subscribe(subscribeRequest);
        });
        
        NotificationResponse notificationResponse = NotificationResponse.builder()
        		.id(getNextId())
        		.title(notificationRequest.getTitle())
        		.message(notificationRequest.getMessage())
        		.build();
        
        String message = getMessage(notificationResponse.getMessage(),
                notificationRequest.getTopicName(),
                notificationRequest.getPlatform());
        
        publish(message, createTopicResult.getTopicArn());

        return notificationRepository.save(notificationResponse);
    }
    
    @Override
	public Flux<NotificationResponse> getAllNotifications() throws InterruptedException, ExecutionException, TimeoutException {
		return notificationRepository.findAllByOrderByIdDesc();
	}

    private String getMessage(String message, String topicName, String platform) throws JsonProcessingException {

        Map<String, Object> actionMap = null;

        Object[] actionArray = {actionMap};

        Map<String, Object> dataObject = new HashMap<>();
        dataObject.put("body", message);
        dataObject.put("actions", actionArray);
        dataObject.put("title", "InspectorApp");
        dataObject.put("notId", 10);
        dataObject.put("android_channel_id", "PushPluginChannel");
        dataObject.put("soundname", "default");
        dataObject.put("style", "inbox");
        dataObject.put("summaryText", "");
        dataObject.put("priority", 2);

        Map<String, Object> platformObject = new HashMap<>();
        platformObject.put("data", dataObject);
        message = objectMapper.writeValueAsString(platformObject);

        Map<String, String> payload = new HashMap<>();
        payload.put("default", topicName);
        payload.put(platform, message);

        return objectMapper.writeValueAsString(payload);
    }

    private CreatePlatformApplicationResult createPlatformApp(
            String appName, String platform, String userData) {
        CreatePlatformApplicationRequest platformApplicationRequest = new CreatePlatformApplicationRequest();
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("PlatformPrincipal", userData);
        attributes.put("PlatformCredential", senderId);
        platformApplicationRequest.setAttributes(attributes);
        platformApplicationRequest.setName(appName);
        platformApplicationRequest.setPlatform(platform);

        return amazonSNS.createPlatformApplication(platformApplicationRequest);
    }

    private PublishResult publish(String message, String arnEndpoint) {
        PublishRequest publishRequest = new PublishRequest();
        publishRequest.setMessageStructure("json");
        publishRequest.setTargetArn(arnEndpoint);
        publishRequest.setMessage(message);
        return amazonSNS.publish(publishRequest);
    }

    private void registerWithSNS(String token, String platformAppArn) {

        String endpointArn = retrieveEndpointArn();

        boolean updateNeeded = false;
        boolean createNeeded = (null == endpointArn);

        if (createNeeded) {
            endpointArn = createEndpoint(token, platformAppArn);
            createNeeded = false;
        }

        try {
            GetEndpointAttributesRequest geaReq =
                    new GetEndpointAttributesRequest()
                            .withEndpointArn(endpointArn);
            GetEndpointAttributesResult geaRes =
                    amazonSNS.getEndpointAttributes(geaReq);

            updateNeeded = !geaRes.getAttributes().get("Token").equals(token)
                    || !geaRes.getAttributes().get("Enabled").equalsIgnoreCase("true");

        } catch (NotFoundException nfe) {
            createNeeded = true;
        }

        if (createNeeded) {
            endpointArn = createEndpoint(token, platformAppArn);
        }

        if (updateNeeded) {
            Map<String, String> attribs = new HashMap<>();
            attribs.put("Token", token);
            attribs.put("Enabled", "true");
            SetEndpointAttributesRequest saeReq =
                    new SetEndpointAttributesRequest()
                            .withEndpointArn(endpointArn)
                            .withAttributes(attribs);
            amazonSNS.setEndpointAttributes(saeReq);
        }
    }

    private String createEndpoint(String token, String platformAppArn) {

        String endpointArn = null;
        try {
            CreatePlatformEndpointRequest cpeReq =
                    new CreatePlatformEndpointRequest()
                            .withPlatformApplicationArn(platformAppArn)
                            .withToken(token);
            CreatePlatformEndpointResult cpeRes = amazonSNS
                    .createPlatformEndpoint(cpeReq);
            endpointArn = cpeRes.getEndpointArn();
        } catch (InvalidParameterException ipe) {
            String message = ipe.getErrorMessage();
            Pattern p = Pattern
                    .compile(".*Endpoint (arn:aws:sns[^ ]+) already exists " +
                            "with the same token.*");
            Matcher m = p.matcher(message);
            if (m.matches()) {
                endpointArn = m.group(1);
            } else {
                throw ipe;
            }
        }
        storeEndpointArn(endpointArn);
        return endpointArn;
    }

    private String retrieveEndpointArn() {
        return arnStorage;
    }

    private void storeEndpointArn(String endpointArn) {
        arnStorage = endpointArn;
    }
	
	private Long getNextId() throws ExecutionException, InterruptedException, TimeoutException {

		Long lastId;

        CompletableFuture<NotificationResponse> future = notificationRepository
                .findTopByOrderByIdDesc()
                .toFuture();
        
        NotificationResponse topicNotificationResponse = future.get(5, TimeUnit.SECONDS);

        if (topicNotificationResponse == null) {
            return 1L;
        }else {
            lastId = topicNotificationResponse.getId();

            return ++lastId;
        }
	}
}
