package com.seth.draft_assistant.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    @GetMapping("/health")
    public String getHealth(){
        return "I am healthy";
    }
}
