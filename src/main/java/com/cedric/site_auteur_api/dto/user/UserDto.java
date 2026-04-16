package com.cedric.site_auteur_api.dto.user;

public record UserDto (
    Integer idUser,
    String username,
    String email,
    Boolean isActive
) {}
