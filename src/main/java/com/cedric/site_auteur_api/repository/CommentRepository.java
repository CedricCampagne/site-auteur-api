package com.cedric.site_auteur_api.repository;
import com.cedric.site_auteur_api.entity.Comment;

import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {
    
    // Méthode custom si besoin
    List<Comment> findByChronicleIdChronicle(Integer idChronicle);
}
