package com.example.ai01.ai.vllm.service;

import com.example.ai01.ai.vllm.config.VllmConfig;
import com.example.ai01.ai.vllm.dto.VllmRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class VllmService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private VllmConfig vllmConfig;

    public String completeText(VllmRequest request ) {
        String result = restTemplate.postForObject(vllmConfig.getBaseUrl() + "/complete", request, String.class);
        return result;
    }
}