package com.cedric.site_auteur_api.controller.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {
    
    @GetMapping("/admin")
    public String dashboard() {
        return "Bienvenu sur le dashboard admin";
    }
}
