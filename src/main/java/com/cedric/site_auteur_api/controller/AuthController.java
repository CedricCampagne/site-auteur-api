package com.cedric.site_auteur_api.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cedric.site_auteur_api.dto.auth.LoginDto;
import com.cedric.site_auteur_api.dto.auth.RegisterDto;
import com.cedric.site_auteur_api.dto.user.UserFullDto;
import com.cedric.site_auteur_api.service.UserService;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    private final UserService userService;

    public AuthController (UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public UserFullDto register (@RequestBody RegisterDto dto) {
        return userService.register(dto);
    }

    @PostMapping("/login")
    public UserFullDto login(@RequestBody LoginDto dto) {
        return userService.login(dto);
    }
    
}
