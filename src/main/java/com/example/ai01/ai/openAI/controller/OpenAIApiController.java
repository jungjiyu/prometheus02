package com.example.ai01.ai.openAI.controller;

import com.example.ai01.ai.openAI.dto.OpenAIRequest;
import com.example.ai01.ai.openAI.service.OpenAIApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/openai")
public class OpenAIApiController {

    private final OpenAIApiService openAIService;

    @PostMapping("/completion")
    public String getOpenAICompletion(@RequestBody OpenAIRequest openAIRequest) throws IOException {
        return openAIService.getCompletion(openAIRequest);
    }
}
