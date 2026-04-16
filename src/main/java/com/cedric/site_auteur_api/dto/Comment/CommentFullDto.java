package com.cedric.site_auteur_api.dto.Comment;

import com.cedric.site_auteur_api.dto.chronicle.ChronicleCommentDto;
import com.cedric.site_auteur_api.dto.user.UserCommentDto;

public record CommentFullDto (
    Integer idComment,
    String content,
    Boolean isVisible,
    UserCommentDto user,
    ChronicleCommentDto chronicle
) {}
