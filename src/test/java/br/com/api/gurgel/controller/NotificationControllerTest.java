package br.com.api.gurgel.controller;

import br.com.api.gurgel.model.NotificationRequest;
import br.com.api.gurgel.model.NotificationResponse;
import br.com.api.gurgel.service.NotificationService;
import br.com.api.gurgel.service.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.javafaker.Faker;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RunWith(SpringRunner.class)
@WebFluxTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class NotificationControllerTest {

    private Faker faker = new Faker();

    private NotificationRequest notificationRequest;

    private NotificationResponse notificationResponse;

    private List<NotificationResponse> notificationResponseList;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private TokenService tokenService;

    @Autowired
    private WebTestClient webClient;

    @Before
    public void setUp() throws Exception {
        notificationRequest = new NotificationRequest("GCM", "Teste", faker.book().title(), faker.book().genre());

        notificationResponse = new NotificationResponse(faker.book().title(), faker.book().genre());

        notificationResponseList = new ArrayList<>();
        notificationResponseList.add(notificationResponse);
    }

    @Test
    public void newTopicNotification() throws InterruptedException, ExecutionException, TimeoutException, JsonProcessingException {

        Mockito.when(notificationService.newTopicNotification(notificationRequest)).thenReturn(Mono.just(notificationResponse));

        webClient.post().uri("/api/notification")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(notificationRequest))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(NotificationResponse.class)
                .isEqualTo(notificationResponse);

    }

    @Test
    public void getAllNotifications() throws InterruptedException, ExecutionException, TimeoutException {

        Mockito.when(notificationService.getAllNotifications()).thenReturn(Flux.fromIterable(notificationResponseList));

        webClient.get().uri("/api/notification").accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(NotificationResponse.class);
    }
}