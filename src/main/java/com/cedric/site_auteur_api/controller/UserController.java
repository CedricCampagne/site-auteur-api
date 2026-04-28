package com.cedric.site_auteur_api.controller;

import com.cedric.site_auteur_api.dto.user.UserFullDto;
import com.cedric.site_auteur_api.service.UserService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/users")
public class UserController {
    
    //injection du service via constructeur
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserFullDto getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }
}
