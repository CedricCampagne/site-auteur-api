package com.cedric.site_auteur_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cedric.site_auteur_api.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    // Méthodes custom si besoins
}  
