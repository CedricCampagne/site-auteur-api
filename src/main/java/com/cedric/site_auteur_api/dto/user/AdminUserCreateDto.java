package com.cedric.site_auteur_api.dto.user;

public record AdminUserCreateDto (
    String username,
    String email,
    String password
) {}

