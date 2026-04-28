package com.cedric.site_auteur_api.dto.user;

public record AdminUserUpdateDto (
    String username,
    String email,
    Boolean isActive,
    String password         // optionnel
) {}