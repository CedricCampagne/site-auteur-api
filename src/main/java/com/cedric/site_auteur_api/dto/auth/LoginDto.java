package com.cedric.site_auteur_api.dto.auth;

public record LoginDto(
    String email,
    String password
) {}
