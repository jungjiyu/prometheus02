package com.example.ai01.ai.openAI.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;



@Configuration
public class OpenAiConfig {

    @Value("${openai.api.key}")
    private String apiKey;
    public String getApiKey() {
        return apiKey;
    }
}
