package com.example.ai01.ai.vllm.service;

import com.example.ai01.ai.vllm.config.VllmConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class VllmService {

    @Autowired
    private VllmConfig vllmConfig;

    public String completeText(String prompt) {
        RestTemplate restTemplate = new RestTemplate();
        String url = vllmConfig.getBaseUrl() + "/complete"; // vLLM 서버의 엔드포인트

        // vLLM 서버로 프롬프트를 전송하고 결과를 받음
        return restTemplate.postForObject(url, prompt, String.class);
    }
}