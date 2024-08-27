package com.example.ai01.ai.groq.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GroqApiConfig {

    @Value("${groq.api.key}")
    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }
}