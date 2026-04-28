package com.cedric.site_auteur_api.dto.auth;

import com.cedric.site_auteur_api.dto.user.UserFullDto;

public record AuthResponse (
    String token,
    UserFullDto user
) {}
