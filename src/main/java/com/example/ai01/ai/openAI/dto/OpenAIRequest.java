package com.example.ai01.ai.openAI.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OpenAIRequest {
    private String model;
    private String prompt;
    private int maxTokens;

}
