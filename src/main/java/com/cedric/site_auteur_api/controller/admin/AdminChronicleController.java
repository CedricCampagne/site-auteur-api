package com.cedric.site_auteur_api.controller.admin;

import com.cedric.site_auteur_api.dto.chronicle.ChronicleCreateDto;
import com.cedric.site_auteur_api.dto.chronicle.AdminChronicleDto;
import com.cedric.site_auteur_api.dto.chronicle.ChronicleUpdateDto;

import com.cedric.site_auteur_api.service.admin.AdminChronicleService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin/chronicles")
public class AdminChronicleController {
    
    private final AdminChronicleService adminChronicleService;

    public AdminChronicleController(AdminChronicleService adminChronicleService) {
        this.adminChronicleService = adminChronicleService;
    }

    @GetMapping
    public List<AdminChronicleDto> getAllChronicles(){
        return adminChronicleService.getAllChronicles();
    }

    @GetMapping("/{id}")
    public AdminChronicleDto getChronicleById(@PathVariable Integer id) {
        return adminChronicleService.getChronicleById(id);
    }

    @PostMapping
    public AdminChronicleDto create(@RequestBody ChronicleCreateDto dto) {
        return adminChronicleService.createChronicle(dto);
    }

    @PatchMapping("/{id}")
    public AdminChronicleDto update(@PathVariable Integer id, @RequestBody ChronicleUpdateDto dto) {
        return adminChronicleService.updateChronicle(id, dto);
    }

    @PatchMapping("/{id}/toggle")
    public AdminChronicleDto toggle(@PathVariable Integer id) {
        return adminChronicleService.toggleChronicle(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        adminChronicleService.deleteChronicle(id);
    }
}
