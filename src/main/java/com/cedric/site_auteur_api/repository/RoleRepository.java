package com.cedric.site_auteur_api.repository;

import com.cedric.site_auteur_api.entity.Role;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    
    Optional<Role> findByRoleName(String roleName);
}
