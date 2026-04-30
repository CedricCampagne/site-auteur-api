package com.cedric.site_auteur_api.dto.chronicle;

import java.time.OffsetDateTime;

public record ChronicleListDto (
    Integer idChronicle,
    String title,
    String coverUrl,
    OffsetDateTime publishedAt,
    String summary
) {}
