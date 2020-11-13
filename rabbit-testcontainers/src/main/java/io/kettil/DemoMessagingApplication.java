package io.kettil;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoMessagingApplication {
    public static void main(String[] args) {
        SpringApplication.run(DemoMessagingApplication.class, args);
    }

    @Bean
    public ApplicationRunner runner(MessageSender messageSender) {
        return args -> {
            messageSender.broadcast("a broadcast message");
            messageSender.sendError("an error message");
            messageSender.broadcast("another broadcast message");
            messageSender.broadcast("that's it");
        };
    }
}
