package com.edoc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.edoc.repository")
@EntityScan(basePackages = "com.edoc.models")
public class EdocApplication {
    public static void main(String[] args) {
        SpringApplication.run(EdocApplication.class, args);
    }
}