package com.cedric.site_auteur_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.cedric.site_auteur_api.entity.Chronicle;
import java.util.List;

@Repository
// JpaRepository<Chronicle, Integer>  <entie, type cle primaire>
public interface ChronicleRepository extends JpaRepository<Chronicle, Integer> {

    // Méthode custom
    Chronicle findBySlug(String slug);

    // SQL natif JPA ne sais pas faire RANDOM
    @Query(value = "SELECT * FROM chronicle ORDER BY RANDOM() LIMIT 3", nativeQuery = true)
    List<Chronicle> findRandom3Chronicles();

    // PAs de sql natif JPA sait : ORDER BY, DESC, LIMIT 3
    List <Chronicle> findTop3ByOrderByPublishedAtDesc();
}
