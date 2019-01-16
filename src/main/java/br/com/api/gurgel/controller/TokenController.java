package br.com.api.gurgel.controller;

import br.com.api.gurgel.model.Token;
import br.com.api.gurgel.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@RequestMapping("/api")
public class TokenController {

    @Autowired
    private TokenService tokenService;

    @CrossOrigin
    @RequestMapping(value = "/token",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Mono<ResponseEntity<Token>> newToken(@RequestBody Token token) throws InterruptedException, ExecutionException, TimeoutException {
        return tokenService.save(token)
                .map(t -> new ResponseEntity(t, HttpStatus.CREATED));
    }

    @CrossOrigin
    @RequestMapping(value = "/token/{id}",
            method = RequestMethod.PUT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Mono<ResponseEntity<Token>> updateToken(@PathVariable("id") Long id,
                                            @RequestBody Token token) {
        return tokenService.update(id, token)
                .map(t -> new ResponseEntity(t, HttpStatus.CREATED));
    }

    @CrossOrigin
    @RequestMapping(value = "/token",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Token> getAllTokens() {
        return tokenService.getAll();
    }

    @RequestMapping(value = "/token/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Mono<ResponseEntity<Token>> getById(@PathVariable("id") Long id) {
        return tokenService.getById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @CrossOrigin
    @RequestMapping(value = "/token/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> deleteById(@PathVariable("id") Long id) {
        tokenService.deleteById(id);
        return Mono.just(new ResponseEntity<>("Deleção bem sucedida!", HttpStatus.ACCEPTED));
    }
}
