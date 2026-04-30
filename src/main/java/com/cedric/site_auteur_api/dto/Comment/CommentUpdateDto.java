package com.cedric.site_auteur_api.dto.Comment;

public record CommentUpdateDto (
    String content,
    Boolean isVisible
) {}
