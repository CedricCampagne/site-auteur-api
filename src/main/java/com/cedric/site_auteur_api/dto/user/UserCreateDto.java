package com.cedric.site_auteur_api.dto.user;

public record UserCreateDto (
    String username,
    String email,
    String password
) {}

