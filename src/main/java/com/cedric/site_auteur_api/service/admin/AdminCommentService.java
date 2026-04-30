package com.cedric.site_auteur_api.service.admin;

import org.springframework.stereotype.Service;

import com.cedric.site_auteur_api.dto.Comment.AdminCommentDto;
import com.cedric.site_auteur_api.dto.Comment.CommentUpdateDto;
import com.cedric.site_auteur_api.mapper.CommentMapper;

import com.cedric.site_auteur_api.repository.CommentRepository;

import com.cedric.site_auteur_api.entity.Comment;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AdminCommentService {

    private final CommentRepository commentRepository;

    public AdminCommentService(
        CommentRepository commentRepository
    ) {
        this.commentRepository = commentRepository;
    }

    //All comments
    public List<AdminCommentDto>getAllComments() {
        return commentRepository.findAll()
            .stream()
            .map(CommentMapper::toAdminDto)
            .toList();
    }

    // comment by id
    public AdminCommentDto getCommentById( Integer id) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(()-> new NoSuchElementException("Commentaire introuvable avec l'id : " + id));
        
        return CommentMapper.toAdminDto(comment);
    }

    //Comments par id de chronique
    public List<AdminCommentDto> getCommentByIdChronicle(Integer idChronicle){
        List<Comment> comments = commentRepository.findByChronicleIdChronicle(idChronicle);

        return comments.stream()
            .sorted((c1, c2)-> c2.getCreatedAt().compareTo(c1.getCreatedAt()))
            .map(CommentMapper::toAdminDto)
            .toList();
    }

    //delete
    public void deleteCommentbyId( Integer id ) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(()-> new NoSuchElementException("Commentaire introuvable avec l'id : " + id));
    
            commentRepository.delete(comment);
    }

    //update
    public AdminCommentDto updateComment( Integer id, CommentUpdateDto dto ) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(()-> new NoSuchElementException("Commentaire introuvable avec l'id : " + id));

        comment.setContent(dto.content());
        comment.setIsVisible(dto.isVisible());
        comment.setUpdatedAt(OffsetDateTime.now());

        Comment updated = commentRepository.save(comment);

        return CommentMapper.toAdminDto(updated);
    }

    //toggle de status
    public AdminCommentDto toggleCommentStatus(Integer id) {
        Comment comment = commentRepository.findById(id)
            .orElseThrow(()-> new NoSuchElementException("Commentaire introuvable avec l'id : " + id));

        comment.setIsVisible(!comment.getIsVisible());
        comment.setUpdatedAt(OffsetDateTime.now());

        Comment updated = commentRepository.save(comment);

        return CommentMapper.toAdminDto(updated);
    }
}
