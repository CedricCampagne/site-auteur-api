package com.cedric.site_auteur_api.controller;

import com.cedric.site_auteur_api.dto.user.UserCreateDto;
import com.cedric.site_auteur_api.dto.user.UserDto;
import com.cedric.site_auteur_api.dto.user.UserFullDto;
import com.cedric.site_auteur_api.dto.user.UserUpdateDto;
import com.cedric.site_auteur_api.service.UserService;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @GetMapping
    public List<UserFullDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public UserFullDto getUserById(@PathVariable Integer id) {
        return userService.getUserById(id);
    }

    @PostMapping
    public UserFullDto createUser(@RequestBody UserCreateDto dto) {
        return userService.createUser(dto);
    }

    @PutMapping("/{id}")
    public UserFullDto updateUser(
        @RequestBody UserUpdateDto dto,
        @PathVariable Integer id){
            return userService.updateUser(id, dto);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
    }

    @PatchMapping("/{id}/toggle")
    public UserFullDto toggleUserStatus(@PathVariable Integer id) {
        return userService.toggleUser(id);
    }

}
