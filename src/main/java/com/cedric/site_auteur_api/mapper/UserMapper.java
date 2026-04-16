package com.cedric.site_auteur_api.mapper;

import com.cedric.site_auteur_api.entity.User;
import com.cedric.site_auteur_api.dto.user.UserDto;
import com.cedric.site_auteur_api.dto.user.UserFullDto;
import com.cedric.site_auteur_api.mapper.RoleMapper;

public class UserMapper {
    
    public static UserDto toDto(User user) {
        return new UserDto(
            user.getIdUser(),
            user.getUsername(),
            user.getEmail(),
            user.getIsActive()
        );
    }

    public static UserFullDto toFullDto(User user) {
        return new UserFullDto(
            user.getIdUser(),
            user.getUsername(),
            user.getEmail(),
            user.getIsActive(),
            user.getUserRoles()
                .stream()
                .map(ur -> RoleMapper.toDto(ur.getRole()))
                .toList()
        );
    }
}
