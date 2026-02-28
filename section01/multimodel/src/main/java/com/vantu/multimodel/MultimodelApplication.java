package com.vantu.multimodel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MultimodelApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultimodelApplication.class, args);
        System.out.println(System.getenv("OPENAI_API_KEY"));
        System.out.println(System.getenv("GENAI_API_KEY"));
    }

}
