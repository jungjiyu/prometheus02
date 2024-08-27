package com.example.ai01.ai.groq.controller;

import com.example.ai01.ai.groq.dto.GroqApiRequest;
import com.example.ai01.ai.groq.service.GroqApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/groq")
public class GroqApiController {

    @Autowired
    private GroqApiService groqApiService;

    @PostMapping("/complete")
    public String completeText(@RequestBody GroqApiRequest request) {
        return groqApiService.completeText(request.getPrompt(), request.getModelType());
    }
}