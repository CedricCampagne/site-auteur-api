package com.cedric.site_auteur_api.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "chronicle")
@NoArgsConstructor
@Getter
@Setter
public class Chronicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_chronicle")
    private Integer idChronicle;

    @Column(nullable = false, unique = true, length = 255)
    private String title;

    @Column(nullable = false, unique = true, length = 255)
    private String slug;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String quote;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String summary;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "cover_url",nullable = false, length = 255)
    private String coverUrl;

    //@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
    @Column(name = "published_at", nullable = false)
    private OffsetDateTime publishedAt;

    @Column(name = "is_active", nullable = false)
    // valeur par default true
    private Boolean isActive = true;

    // pas @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX") pas envoyée par le client au create
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    // Les relations

    @OneToMany(mappedBy = "chronicle", cascade = CascadeType.ALL)
    @JsonManagedReference("chronicle-comments")
    private List<Comment> comments;

    // Méthodes utilitaires (optionnel)

    // avant INSERT
    @PrePersist
    protected void onCreate() {
        createdAt = updatedAt = OffsetDateTime.now();
        if(isActive == null) {
            isActive = true;
        }
        generateSlug();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
        generateSlug(); // régénère le slug si le titre change
    }

    // Méthode utilitaire pour générer le slug
        private void generateSlug() {
            if (title != null && !title.isEmpty()) {
                // par exemple remplacer les espaces par des tirets et tout mettre en minuscule
                slug = title.toLowerCase().replaceAll("[^a-z0-9]+", "-").replaceAll("-$", "");
            }
        }
}

