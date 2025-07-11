package com.example.cukllteam3.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MainController {
    @GetMapping("/test")
    public String home() {
        return "Hello, CI/CD! Welcome to CUKLLteam3!";

    }

}
