package br.com.api.gurgel.controller;

import br.com.api.gurgel.model.Token;
import br.com.api.gurgel.service.NotificationService;
import br.com.api.gurgel.service.TokenService;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@WebFluxTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TokenControllerTest {

    private Faker faker = new Faker();

    private Map<Long, Token> tokenMap = new HashMap<>();

    private Long id1, id2, id3;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private TokenService tokenService;

    @Autowired
    private WebTestClient webClient;

    @Before
    public void setUp() throws Exception {
        id1 = faker.number().randomNumber();
        tokenMap.put(id1, new Token(id1, faker.idNumber().valid(), faker.idNumber().valid()));
        id2 = faker.number().randomNumber();
        tokenMap.put(id2, new Token(id2, faker.idNumber().valid(), faker.idNumber().valid()));
        id3 = faker.number().randomNumber();
        tokenMap.put(id3, new Token(id3, faker.idNumber().valid(), faker.idNumber().valid()));
    }

    @Test
    public void newToken() throws InterruptedException, ExecutionException, TimeoutException {

        Mockito.when(tokenService.save(tokenMap.get(id1))).thenReturn(Mono.just(tokenMap.get(id1)));

        webClient.post().uri("/api/token")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(tokenMap.get(id1)))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Token.class)
                .isEqualTo(tokenMap.get(id1));
    }

    @Test
    public void updateToken() {

        Token token = tokenMap.get(id1);

        Mockito.when(tokenService.update(id2, token)).thenReturn(Mono.just(token));

        webClient.put().uri("/api/token/{id}", id2)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(token))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Token.class)
                .isEqualTo(token);
    }

    @Test
    public void getAllTokens() {

        Mockito.when(tokenService.getAll())
                .thenReturn(Flux
                        .fromIterable(tokenMap.entrySet().stream()
                                .map(token -> token.getValue())
                                .collect(Collectors.toList())));

        webClient.get().uri("/api/token")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Token.class)
                .hasSize(3)
                .contains(tokenMap.get(id1), tokenMap.get(id2), tokenMap.get(id3));
    }

    @Test
    public void getById() {

        Token token = tokenMap.get(id1);

        Mockito.when(tokenService.getById(id1)).thenReturn(Mono.just(token));

        webClient.get().uri("/api/token/{id}", id1)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Token.class)
                .isEqualTo(token);
    }

    @Test
    public void deleteById() {

        webClient.delete().uri("/api/token/{id}", id1)
                .exchange()
                .expectStatus().isAccepted()
                .expectBody(String.class)
                .isEqualTo("Deleção bem sucedida!");
    }
}