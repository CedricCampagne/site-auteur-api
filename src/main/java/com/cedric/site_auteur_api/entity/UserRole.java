package com.cedric.site_auteur_api.entity;

import java.time.OffsetDateTime;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Table(name = "user_role")
@Getter
@Setter
@NoArgsConstructor
public class UserRole {
    
    @EmbeddedId
    private UserRoleId id = new UserRoleId();

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "id_user")
    private User user;

    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name = "id_role")
    private Role role;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

}
