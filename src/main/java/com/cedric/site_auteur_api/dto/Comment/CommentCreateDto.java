package com.cedric.site_auteur_api.dto.Comment;

public record CommentCreateDto (
    String content,
    Boolean isVisible,
    Integer idUser,
    Integer idChronicle
) {}
