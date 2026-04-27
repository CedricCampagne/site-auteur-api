package com.cedric.site_auteur_api.repository;
import com.cedric.site_auteur_api.entity.UserRole;
import com.cedric.site_auteur_api.entity.UserRoleId;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
    
}
