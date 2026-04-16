package com.cedric.site_auteur_api.mapper;

import com.cedric.site_auteur_api.entity.User;
import com.cedric.site_auteur_api.dto.user.UserDto;

public class UserMapper {
    
    public static UserDto toDto(User user) {
        return new UserDto(
            user.getIdUser(),
            user.getUsername(),
            user.getEmail(),
            user.getIsActive()
        );
    }
}
