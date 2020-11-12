package io.kettil.fluxtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class FluxTestApplication {
    public static void main(String[] args) throws InterruptedException {
        SpringApplication app = new SpringApplication(FluxTestApplication.class);
        app.setWebApplicationType(WebApplicationType.NONE);
        app.run(args);

        Thread.currentThread().join();
    }
}
