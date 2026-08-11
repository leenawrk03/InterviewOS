package dev.interviewos.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class InterviewOsApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterviewOsApiApplication.class, args);
    }
}
