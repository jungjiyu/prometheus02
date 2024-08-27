package com.example.ai01.ai.groq.service;

import com.example.ai01.ai.groq.config.GroqApiConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GroqApiService {

    @Autowired
    private GroqApiConfig groqApiConfig;

    public String completeText(String prompt, String modelType) {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://api.groq.com/openai/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + groqApiConfig.getApiKey());
        headers.set("Content-Type", "application/json");

        String requestJson = "{\"messages\": [{\"role\": \"user\", \"content\": \""+prompt+"\"}], \"model\": \""+modelType+"\"}";
        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        return restTemplate.exchange(url, HttpMethod.POST, entity, String.class).getBody();
    }
}