package br.com.api.gurgel.repository;

import br.com.api.gurgel.model.Token;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface TokenRepository extends ReactiveCrudRepository<Token, Long> {
	Mono<Token> findTopByOrderByIdDesc();
	
	Mono<Token> findByDeviceId(String deviceId);
}
