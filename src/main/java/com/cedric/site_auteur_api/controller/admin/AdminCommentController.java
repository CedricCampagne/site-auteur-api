package com.cedric.site_auteur_api.controller.admin;

import com.cedric.site_auteur_api.dto.Comment.AdminCommentDto;
import com.cedric.site_auteur_api.dto.Comment.CommentUpdateDto;

import com.cedric.site_auteur_api.service.admin.AdminCommentService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@RestController
@RequestMapping("/admin/comments")
public class AdminCommentController {
    
    private final AdminCommentService adminCommentService;

    public AdminCommentController(AdminCommentService adminCommentService) {
        this.adminCommentService = adminCommentService;
    }

    @GetMapping
    public List<AdminCommentDto> getAllComments() {
        return adminCommentService.getAllComments();
    }
    
    @GetMapping("/{id}")
    public AdminCommentDto getCommentById(@PathVariable Integer id) {
        return adminCommentService.getCommentById(id);
    }

    @GetMapping("/chronicle/{id}")
    public List<AdminCommentDto> getCommentByIdChronicle(@PathVariable Integer idChronicle) {
        return adminCommentService.getCommentByIdChronicle(idChronicle);
    }

    @DeleteMapping("/{id}")
    public void deleteCommentbyId(@PathVariable Integer id) {
        adminCommentService.deleteCommentbyId(id);
    }

    @PatchMapping("/{id}")
    public AdminCommentDto updateComment(
        @PathVariable Integer id,
        @RequestBody CommentUpdateDto dto
    ) {
        return adminCommentService.updateComment(id, dto);
    }

    @PatchMapping("/{id}/toggle")
    public AdminCommentDto toggleCommentStatus(@PathVariable Integer id) {
        return adminCommentService.toggleCommentStatus(id);
    }

    
}
