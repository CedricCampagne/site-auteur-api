package com.cedric.site_auteur_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cedric.site_auteur_api.entity.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Méthodes custom si besoins

    // verifier si email est deja en bdd
    boolean existsByEmail(String email);

    // verifier si username est deja en bdd
    boolean existsByUsername(String username);

    //Objet qui peut contenir : un user ou null
     Optional<User> findByEmail(String email);
}  
