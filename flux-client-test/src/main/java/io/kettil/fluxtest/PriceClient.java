package io.kettil.fluxtest;

import org.springframework.web.reactive.function.client.WebClient;

public class PriceClient {
    WebClient client = WebClient.create("http://localhost:8080");


}
