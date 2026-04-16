package com.cedric.site_auteur_api.dto.chronicle;

import java.time.OffsetDateTime;

public record ChronicleDto (
    Integer idChronicle,
    String title,
    String slug,
    String quote,
    String summary,
    String content,
    String coverUrl,
    OffsetDateTime publishedAt,
    Boolean isActive
) {}
