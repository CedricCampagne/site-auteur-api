package com.cedric.site_auteur_api.controller;

import com.cedric.site_auteur_api.dto.chronicle.ChronicleCreateDto;
import com.cedric.site_auteur_api.dto.chronicle.ChronicleDto;
import com.cedric.site_auteur_api.dto.chronicle.ChronicleUpdateDto;

import com.cedric.site_auteur_api.service.ChronicleService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/chronicles")
public class ChronicleController {

    private final ChronicleService chronicleService;

    // Injection du service via constructeur
    public ChronicleController(ChronicleService chronicleService) {
        this.chronicleService = chronicleService;
    }

    @GetMapping
    public List<ChronicleDto> getAllChronicles() {
        return chronicleService.getAllChronicles();
    }
    
    @GetMapping("/slug/{slug}")
    public ChronicleDto getChronicleBySlug(@PathVariable String slug) {
        return chronicleService.getChronicleBySlug(slug);
    }

    @GetMapping("/id/{id}")
    public ChronicleDto getChronicleById(@PathVariable Integer id) {
        return chronicleService.getChronicleById(id);
    }

    @GetMapping("/random")
    public List<ChronicleDto> getRandomChronicles() {
        return chronicleService.getRandom3Chronicles();
    }

    @GetMapping("/latest")
    public List<ChronicleDto>get3LatestChronicles() {
        return chronicleService.get3LatestChronicles();
    }

    @DeleteMapping("/{id}")
    public Map<String, String>deleteChronicle(@PathVariable Integer id) {
        chronicleService.deleteChronicleById(id);
        return Map.of("message", "Chronique supprimée avec succès");
    }

    @PutMapping("/id/{id}")
    public ChronicleDto updateChronicle(
        @PathVariable Integer id,
        @RequestBody ChronicleUpdateDto data ) {
            return chronicleService.updateChronicle(id, data);
    }

    @PatchMapping("/id/{id}/toggle")
    public ChronicleDto toggleChronicle(@PathVariable Integer id) {
        return chronicleService.toggleChronicle(id);
    }

    @PostMapping
    public ChronicleDto creatChronicle(@RequestBody ChronicleCreateDto data) {
        return chronicleService.createChronicle(data);
    }
}
