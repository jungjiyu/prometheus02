package com.example.ai01.ai.vllm.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VllmConfig {

    @Value("${vllm.base.url}")
    private String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }
}