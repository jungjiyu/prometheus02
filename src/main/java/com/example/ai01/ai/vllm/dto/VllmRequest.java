package com.example.ai01.ai.vllm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class VllmRequest {

    private String prompt;
    private int maxTokens;

}
