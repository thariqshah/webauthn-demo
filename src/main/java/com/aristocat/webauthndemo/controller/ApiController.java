package com.aristocat.webauthndemo.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
public class ApiController {

    @GetMapping("/api/time")
    public String getTime() {
        return "Current time: " + LocalDateTime.now();
    }
}