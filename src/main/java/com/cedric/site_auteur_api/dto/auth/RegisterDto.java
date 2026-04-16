package com.cedric.site_auteur_api.dto.auth;

public record RegisterDto (
    String username,
    String email,
    String password
) {}
