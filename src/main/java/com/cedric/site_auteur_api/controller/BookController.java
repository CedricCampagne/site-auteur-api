package com.cedric.site_auteur_api.controller;

import com.cedric.site_auteur_api.dto.book.BookDto;

import com.cedric.site_auteur_api.entity.Book;

import com.cedric.site_auteur_api.service.BookService;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    // Injection du service via constructeur
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // GET /books : tous les livres
    @GetMapping
    public List<BookDto> getAllBooks() {
        return bookService.getAllBooks();
    }

    // GET /books/{slug}  livre par slug
    @GetMapping("/{slug}")
    public Book getBookBySlug(@PathVariable String slug) {
        return bookService.getBookBySlug(slug);
    }
    
    // GET /books/id/{id}  livre par ID
    @GetMapping("/id/{id}")
    public Book getBookById(@PathVariable Integer id) {
        return bookService.getBookById(id);
    }

}
