package com.cedric.site_auteur_api.dto.user;

import com.cedric.site_auteur_api.dto.role.RoleDto;

import java.util.List;

public record UserFullDto (
    Integer idUser,
    String username,
    String email,
    Boolean isActive,
    List<RoleDto> roles
) {}
