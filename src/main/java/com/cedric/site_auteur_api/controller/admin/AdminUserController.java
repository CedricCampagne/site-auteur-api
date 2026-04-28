package com.cedric.site_auteur_api.controller.admin;

import com.cedric.site_auteur_api.dto.user.AdminUserUpdateDto;
import com.cedric.site_auteur_api.dto.user.UserFullDto;
import com.cedric.site_auteur_api.service.AdminUserService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
public class AdminUserController {
    
    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public List<UserFullDto> getAllUsers() {
        return adminUserService.getAllUsers();
    }
    
    @PatchMapping("/{id}")
    public UserFullDto updateUser(
        @PathVariable Integer id,
        @RequestBody AdminUserUpdateDto dto
    ) {
        return adminUserService.updateUser(id, dto);
    }

    @PatchMapping("/{id}/toggle")
    public UserFullDto toggleUser(@PathVariable Integer id) {
        return adminUserService.toggleUser(id);
    }
    
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Integer id) {
        adminUserService.deleteUser(id);
    }
}

