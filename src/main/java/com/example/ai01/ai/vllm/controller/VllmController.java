package com.example.ai01.ai.vllm.controller;

import com.example.ai01.ai.vllm.service.VllmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vllm")
public class VllmController {

    @Autowired
    private VllmService vllmService;

    @PostMapping("/complete")
    public String completeText(@RequestBody String prompt) {
        return vllmService.completeText(prompt);
    }
}