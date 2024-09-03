package com.example.ai01.ai.vllm.controller;

import com.example.ai01.ai.vllm.dto.VllmRequest;
import com.example.ai01.ai.vllm.service.VllmService;
import io.micrometer.core.annotation.Timed;
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
    public String completeText(@RequestBody VllmRequest request) {
        return vllmService.completeText(request);
    }
}