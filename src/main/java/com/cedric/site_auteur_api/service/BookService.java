package com.cedric.site_auteur_api.service;

import com.cedric.site_auteur_api.entity.Book;
import com.cedric.site_auteur_api.repository.BookRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class BookService {
    
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    // Récupérer tous les livres
    public List<Book> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        if (books.isEmpty()) {
            throw new NoSuchElementException("Aucun livre trouvé");
        }
        return books;
    }

    // Récupérer un livre par slug
    public Book getBookBySlug(String slug) {
        Book book = bookRepository.findBySlug(slug);
        if (book == null) {
            throw new NoSuchElementException("Livre non trouvé avec le slug : " + slug);
        }
        return book;
    }

    // Récupérer un livre par ID
    public Book getBookById(Integer id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Livre non trouvé avec l'ID : " + id));
    }

    // Mettre à jour un livre
    public Book updateBook(Book book) {
        if (book.getIdBook() == null || !bookRepository.existsById(book.getIdBook())) {
            throw new NoSuchElementException("Impossible de mettre à jour : livre inexistant");
        }
        return bookRepository.save(book);
    }

    // Supprimer un livre
    public void deleteBook(Integer id) {
        if (!bookRepository.existsById(id)) {
            throw new NoSuchElementException("Impossible de supprimer : livre inexistant avec l'ID " + id);
        }
        bookRepository.deleteById(id);
    }
}
