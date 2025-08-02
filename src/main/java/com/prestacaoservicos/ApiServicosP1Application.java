package com.prestacaoservicos;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ApiServicosP1Application {
    public static void main(String[] args) {
        Dotenv.load();
        SpringApplication.run(ApiServicosP1Application.class, args);
    }

}
