package br.com.api.gurgel.controller;

import br.com.api.gurgel.model.NotificationRequest;
import br.com.api.gurgel.model.NotificationResponse;
import br.com.api.gurgel.service.NotificationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @CrossOrigin
    @RequestMapping(value = "/notification",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Mono<ResponseEntity<NotificationResponse>> newTopicNotification(@Valid @RequestBody NotificationRequest notificationRequest) throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException {

        return notificationService.newTopicNotification(notificationRequest)
                .map(notificationResponse -> new ResponseEntity(notificationResponse, HttpStatus.CREATED));
    }

    @CrossOrigin
    @RequestMapping(value = "/notification",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Flux<NotificationResponse> getAllNotifications() throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException {
        return notificationService.getAllNotifications();
    }

}
