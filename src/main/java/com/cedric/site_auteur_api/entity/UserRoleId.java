package com.cedric.site_auteur_api.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

// cette classe peut être INSÉRÉE dans une autre entity
//ce n’est pas une table, c’est une partie de clé
@Embeddable
// implements Serializable = la clé sera stockée / comparée / sérialisée par JPA
public class UserRoleId implements Serializable {

    // Les champs
    private Integer userId;
    private Integer roleId;

    // Constructeur
    public UserRoleId() {}

    public UserRoleId(Integer userId, Integer roleId) {
        this.userId = userId;
        this.roleId = roleId;
    }

    // Getters
    public Integer getUserId() {
        return userId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    // "2 clés sont identiques si userId + roleId sont identiques"
    // voir notes
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserRoleId)) return false;
        UserRoleId that = (UserRoleId) o;
        return Objects.equals(userId, that.userId) &&
               Objects.equals(roleId, that.roleId);
    }
    
    // optimisation mémoire + collections + Hibernate
    // voir notes
    @Override
    public int hashCode() {
        return Objects.hash(userId, roleId);
    }
}
