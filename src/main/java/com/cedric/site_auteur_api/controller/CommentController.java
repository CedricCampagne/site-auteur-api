package com.cedric.site_auteur_api.controller;

import com.cedric.site_auteur_api.repository.CommentRepository;

import com.cedric.site_auteur_api.service.CommentService;

import com.cedric.site_auteur_api.dto.Comment.CommentFullDto;
import com.cedric.site_auteur_api.dto.Comment.CommentCreateDto;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {
    
    private final CommentService commentService;

    public CommentController(CommentService commentService, CommentRepository commentRepository){
        this.commentService = commentService;
    }

    @GetMapping
    public List<CommentFullDto> getAllComments(){
        return commentService.getAllComment();
    }

    @GetMapping("/{id}")
    public CommentFullDto getCommentById(@PathVariable Integer id) {
        return commentService.getCommentById(id);
    }

    @GetMapping("/chronicle/{id}")
    public List<CommentFullDto> getCommentByIdChronicle(@PathVariable Integer id) {
        return commentService.getCommentByIdChronicle(id);
    }

    @PostMapping
    public CommentFullDto createComment(@RequestBody CommentCreateDto dto) {
        return commentService.createComment(dto);
    }
}
