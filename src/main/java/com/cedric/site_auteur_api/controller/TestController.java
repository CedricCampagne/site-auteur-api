package com.cedric.site_auteur_api.controller;

import com.cedric.site_auteur_api.entity.Book;
import com.cedric.site_auteur_api.repository.BookRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestController {

    private final BookRepository bookRepository;

    public TestController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @GetMapping("/books")
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @GetMapping("/books/slug/{slug}")
    public Book getBookBySlug(@PathVariable String slug) {
    
        return bookRepository.findBySlug(slug);
        
    }
}
