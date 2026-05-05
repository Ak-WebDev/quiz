package com.quizapp.quizbackend.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI quizAppOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("QuizApp API")
                        .description("Online Quiz Application backend API")
                        .version("1.0.0"));
    }
}