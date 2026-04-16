package com.cedric.site_auteur_api.controller;

import com.cedric.site_auteur_api.repository.CommentRepository;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.cedric.site_auteur_api.dto.Comment.CommentFullDto;
import com.cedric.site_auteur_api.dto.Comment.CommentUpdateDto;
import com.cedric.site_auteur_api.dto.Comment.CommentCreateDto;
import com.cedric.site_auteur_api.service.CommentService;

@RestController
@RequestMapping("/comments")
public class CommentController {
    
    private final CommentRepository commentRepository;
    private final CommentService commentService;

    public CommentController(CommentService commentService, CommentRepository commentRepository){
        this.commentService = commentService;
        this.commentRepository = commentRepository;
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

    // version RESTful direct si ok 204 dlete reussi pas de message avec Map voir ChronicleController
    @DeleteMapping("{id}")
    public void deleteCommentbyId(@PathVariable Integer id) {
        commentService.deleteCommentbyId(id);
    }

    @PostMapping
    public CommentFullDto createComment(@RequestBody CommentCreateDto dto) {
        return commentService.createComment(dto);
    }

    @PutMapping("/{id}")
    public CommentFullDto updateComment(
        @PathVariable Integer id,
        @RequestBody CommentUpdateDto dto) {
            return commentService.updateComment(id, dto);
        }

    @PatchMapping("/{id}/toggle")
    public CommentFullDto toggleComment(@PathVariable Integer id) {
        return commentService.toggleCommentStatus(id);
    }
}
