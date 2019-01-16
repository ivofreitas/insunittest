package br.com.api.gurgel.service;

import br.com.api.gurgel.model.NotificationRequest;
import br.com.api.gurgel.model.NotificationResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface NotificationService {

    Mono<NotificationResponse> newTopicNotification(NotificationRequest topicNotificationRequest) throws JsonProcessingException, InterruptedException, ExecutionException, TimeoutException;
    
    Flux<NotificationResponse> getAllNotifications() throws InterruptedException, ExecutionException, TimeoutException;
}
