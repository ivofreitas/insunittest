package br.com.api.gurgel.service.impl;

import br.com.api.gurgel.model.Token;
import br.com.api.gurgel.repository.TokenRepository;
import br.com.api.gurgel.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class TokenServiceImpl implements TokenService {

	@Autowired
	private TokenRepository tokenRepository;

	@Override
	public Mono<Token> save(Token token) throws InterruptedException, ExecutionException, TimeoutException {

		token.setId(getNextId(token.getDeviceId()));
		return tokenRepository.save(token);
	}

	@Override
	public Mono<Token> update(Long id, Token token) {

		return tokenRepository.findById(id).map(t -> {
			t.setTokenKey(token.getTokenKey());
			return t;
		}).flatMap(t -> tokenRepository.save(t));
	}

	@Override
	public Mono<Token> getById(Long id) {
		return tokenRepository.findById(id);
	}

	@Override
	public Flux<Token> getAll() {
		return tokenRepository.findAll();
	}

	private Long getNextId(String deviceId) throws ExecutionException, InterruptedException, TimeoutException {

		Long lastId;

        CompletableFuture<Token> tokenCompletableFuture = tokenRepository
                .findByDeviceId(deviceId)
                .toFuture();

        if (tokenCompletableFuture.get(5, TimeUnit.SECONDS) == null) {
            tokenCompletableFuture =  tokenRepository
                    .findTopByOrderByIdDesc()
                    .toFuture();

            if(tokenCompletableFuture.get(5, TimeUnit.SECONDS) == null) {
                return 1L;
            }
            lastId = tokenCompletableFuture.get().getId();

            return ++lastId;
        }else {
            lastId = tokenCompletableFuture.get().getId();

            return lastId;
        }
	}

	@Override
	public Mono<Void> deleteById(Long id) {
		return tokenRepository.deleteById(id);
	}

}
