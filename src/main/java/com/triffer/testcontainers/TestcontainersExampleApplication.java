package com.triffer.testcontainers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.triffer.testcontainers.ui")
public class TestcontainersExampleApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestcontainersExampleApplication.class, args);
    }

}

