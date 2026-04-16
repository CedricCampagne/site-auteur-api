package com.cedric.site_auteur_api.mapper;

import com.cedric.site_auteur_api.dto.book.BookDto;
import com.cedric.site_auteur_api.entity.Book;

public class BookMapper {
    
    public static BookDto tDto(Book book) {
        return  new BookDto(
            book.getIdBook(),
            book.getTitle(),
            book.getSlug(),
            book.getAuthor(),
            book.getSummary(),
            book.getExcerpt(),
            book.getPublishedAt(),
            book.getPublisher(),
            book.getGenre(),
            book.getCoverUrl(),
            book.getIsActive()
        );   
    }
}
