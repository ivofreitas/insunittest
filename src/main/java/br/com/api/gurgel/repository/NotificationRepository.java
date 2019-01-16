package br.com.api.gurgel.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import br.com.api.gurgel.model.NotificationResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface NotificationRepository extends ReactiveCrudRepository<NotificationResponse, Long> {
	Mono<NotificationResponse> findTopByOrderByIdDesc();
	Flux<NotificationResponse> findAllByOrderByIdDesc();
}
