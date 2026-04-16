package com.cedric.site_auteur_api.mapper;

import com.cedric.site_auteur_api.dto.role.RoleDto;
import com.cedric.site_auteur_api.entity.Role;

public class RoleMapper {
    
    public static RoleDto toDto(Role role) {
        return new RoleDto(
            role.getIdRole(),
            role.getRoleName()
        );
    }
}
