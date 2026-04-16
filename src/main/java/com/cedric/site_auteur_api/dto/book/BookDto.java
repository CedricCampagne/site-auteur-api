package com.cedric.site_auteur_api.dto.book;

import java.time.OffsetDateTime;

public record BookDto (
    Integer idBook,
    String title,
    String slug,
    String author,
    String summary,
    String excerpt,
    OffsetDateTime publishedAt,
    String publisher,
    String genre,
    String coverUrl,
    Boolean isActive
) {}
