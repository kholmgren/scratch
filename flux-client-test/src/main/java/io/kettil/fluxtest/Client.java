package io.kettil.fluxtest;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Component
public class Client {
    WebClient client = WebClient.create("http://localhost:8080");

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        Flux<Price> priceFlux = client.get()
                .uri("/stream")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .retrieve()
                .bodyToFlux(Price.class);

        priceFlux.subscribe(System.out::println);

    }
}
