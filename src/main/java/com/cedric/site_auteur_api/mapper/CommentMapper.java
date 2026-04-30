package com.cedric.site_auteur_api.mapper;

import com.cedric.site_auteur_api.dto.Comment.AdminCommentDto;
import com.cedric.site_auteur_api.dto.Comment.CommentDto;
import com.cedric.site_auteur_api.dto.Comment.CommentFullDto;
import com.cedric.site_auteur_api.dto.chronicle.ChronicleCommentDto;
import com.cedric.site_auteur_api.dto.user.UserCommentDto;
import com.cedric.site_auteur_api.entity.Comment;

public class CommentMapper {
    
    public static CommentDto toDto(Comment comment) {
        return new CommentDto(
            comment.getIdComment(),
            comment.getContent(),
            comment.getIsVisible()
        );
    }

    public static CommentFullDto toFullDto( Comment comment) {
        return new CommentFullDto(
            comment.getIdComment(),
            comment.getContent(),
            comment.getIsVisible(),
            new UserCommentDto(
                comment.getUser().getIdUser(),
                comment.getUser().getUsername()
            ),
            new ChronicleCommentDto(
                comment.getChronicle().getIdChronicle(),
                comment.getChronicle().getTitle()
            )
        );
    }

    public static AdminCommentDto toAdminDto(Comment c) {
    return new AdminCommentDto(
        c.getIdComment(),
        c.getContent(),
        c.getIsVisible(),
        c.getCreatedAt(),
        c.getUpdatedAt(),
        new UserCommentDto(
            c.getUser().getIdUser(),
            c.getUser().getUsername()
        ),
        new ChronicleCommentDto(
            c.getChronicle().getIdChronicle(),
            c.getChronicle().getTitle()
        )
    );
}

}
