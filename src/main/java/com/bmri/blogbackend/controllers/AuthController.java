package com.bmri.blogbackend.controllers;

import com.bmri.blogbackend.dtos.request.LoginRequestDto;
import com.bmri.blogbackend.dtos.response.JwtResponseDto;
import com.bmri.blogbackend.repositories.UserRepository;
import com.bmri.blogbackend.utils.JwtUtils;
import com.bmri.blogbackend.utils.PasswordEncoderUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AuthController {

    private UserRepository userRepository;
    private JwtUtils jwtUtils;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequest) {
        return userRepository.findByUsername(loginRequest.getUsername())
                .filter(user -> PasswordEncoderUtils.matches(loginRequest.getPassword(), user.getPassword()))
                .map(user -> ResponseEntity.ok(new JwtResponseDto(jwtUtils.generateToken(user.getUsername(), user.role))))
                .orElseGet(() -> ResponseEntity.status(401).body(new JwtResponseDto("Invalid credentials")));
    }
}
