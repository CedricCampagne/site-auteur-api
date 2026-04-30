package com.cedric.site_auteur_api.mapper;

import com.cedric.site_auteur_api.dto.chronicle.AdminChronicleDto;
import com.cedric.site_auteur_api.dto.chronicle.ChronicleDto;
import com.cedric.site_auteur_api.entity.Chronicle;

public class ChronicleMapper {
    
    public static ChronicleDto toDto(Chronicle chronicle) {
        return new ChronicleDto(
            chronicle.getIdChronicle(),
            chronicle.getTitle(),
            chronicle.getSlug(),
            chronicle.getQuote(),
            chronicle.getSummary(),
            chronicle.getContent(),
            chronicle.getCoverUrl(),
            chronicle.getPublishedAt(),
            chronicle.getIsActive()
        );
    }

    public static AdminChronicleDto toAdminDto(Chronicle c) {
        return new AdminChronicleDto(
            c.getIdChronicle(),
            c.getTitle(),
            c.getSlug(),
            c.getQuote(),
            c.getSummary(),
            c.getContent(),
            c.getCoverUrl(),
            c.getPublishedAt(),
            c.getIsActive(),
            c.getCreatedAt(),
            c.getUpdatedAt()
        );
    }
}