package com.cedric.site_auteur_api.dto.Comment;


public record CommentDto (
    Integer idComment,
    String content,
    Boolean isVisible

) {}

