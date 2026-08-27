package org.gd.ws2.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.gd.ws2.Entity.User;
import org.gd.ws2.Entity.dto.LoginRequest;
import org.gd.ws2.Entity.dto.RegisterRequest;
import org.gd.ws2.Service.JwtService;
import org.gd.ws2.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequest request) {
        if(userRepository.findByUsername(request.getUsername()).isPresent()) {return "Username already exists";}
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.save(user);

        return "User registered successfully";
    }
    @PostMapping("/login")
    public String login(
            @Valid @RequestBody LoginRequest request) {
    User user = userRepository.findByUsername(request.getUsername()).orElseThrow(()->new RuntimeException("Username not found"));
    if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {return "Wrong password";}
        return jwtService.generateToken(
                user.getUsername()
        );
    }


}
