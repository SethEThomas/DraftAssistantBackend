package com.seth.draft_assistant.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthCheckController {
    @GetMapping("/health")
    public ResponseEntity<String> getHealth(){
        return new ResponseEntity<>("I am very healthy", HttpStatus.OK);
    }
}
