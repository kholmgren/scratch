//package io.kettil.fluxtest;
//
//import org.springframework.http.MediaType;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//import reactor.core.publisher.Flux;
//
//import java.time.Duration;
//import java.util.concurrent.ThreadLocalRandom;
//
//@RestController
//public class PriceController {
//
//    /**
//     * curl -i localhost:8080/stream
//     *
//     * @return
//     */
//    @GetMapping(value = "/stream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
//    public Flux<Price> priceStream() {
//        return Flux
//                .interval(Duration.ofMillis(500))
//                .map(l -> new Price(
//                        System.currentTimeMillis(),
//                        ThreadLocalRandom.current().nextInt(100, 125)))
//                .limitRequest(10)
//
//                .doOnSubscribe(s -> {
//                    System.out.println("---subscribing");
//                })
//                .doOnCancel(() -> {
//                    System.out.println("---cancelled");
//                })
//                .doOnComplete(() -> {
//                    System.out.println("---completed");
//                })
//
//                .log();
//    }
//
//}
