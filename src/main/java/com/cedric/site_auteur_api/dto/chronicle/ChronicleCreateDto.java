package com.cedric.site_auteur_api.dto.chronicle;

import java.time.OffsetDateTime;

public record ChronicleCreateDto (
    String title,
    String quote,
    String summary,
    String content,
    String coverUrl,
    OffsetDateTime publishedAt,
    Boolean isActive
){}
