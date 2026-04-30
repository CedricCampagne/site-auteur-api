package com.cedric.site_auteur_api.dto.Comment;

import com.cedric.site_auteur_api.dto.chronicle.ChronicleCommentDto;
import com.cedric.site_auteur_api.dto.user.UserCommentDto;

import java.time.OffsetDateTime;

public record AdminCommentDto (
    Integer idComment,
    String content,
    Boolean isVisible,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt,
    UserCommentDto user,
    ChronicleCommentDto chronicle
) {}
