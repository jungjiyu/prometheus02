package com.example.ai01.ai.groq.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GroqApiRequest {
    private String prompt;
    private String modelType;
}
