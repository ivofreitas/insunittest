package br.com.api.gurgel.service;

import br.com.api.gurgel.model.Token;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface TokenService {

    Mono<Token> save(Token token) throws InterruptedException, ExecutionException, TimeoutException;

    Mono<Token> update(Long id, Token token);

    Mono<Token> getById(Long id);

    Flux<Token> getAll();

    Mono<Void> deleteById(Long id);
}
