package com.flightbooking.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller //a smarter man than me advised me to make this one idk why forgot to ask
public class HomeController {
    
    @GetMapping("/")
    public String home() {
        return "redirect:/index.html";
    }
} 