package com.cedric.site_auteur_api.repository;

import com.cedric.site_auteur_api.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository; 
import org.springframework.stereotype.Repository;

@Repository
// Book = entité, Integer = type de clé primaire.
public interface BookRepository extends JpaRepository<Book, Integer> {

    // Méthode custom pour retrouver un livre par son slug
    Book findBySlug(String slug);
}
