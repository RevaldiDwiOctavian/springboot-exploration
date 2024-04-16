package com.example.api.demoapi.controller;

import com.example.api.demoapi.dto.request.LoginRequest;
import com.example.api.demoapi.dto.request.RegisterRequest;
import com.example.api.demoapi.dto.response.AuthResponse;
import com.example.api.demoapi.dto.response.ResponseMessage;
import com.example.api.demoapi.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RestControllerAdvice
@RequestMapping("/api/auth")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ResponseMessage> createUser(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return  userService.login(request);
    }

    @GetMapping("/home")
    public void home() {

    }
}
