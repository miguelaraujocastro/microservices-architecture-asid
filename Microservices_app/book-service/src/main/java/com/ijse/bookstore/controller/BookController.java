package com.ijse.bookstore.controller;

import com.ijse.bookstore.entity.Book;
import com.ijse.bookstore.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class BookController {

    @Autowired
    private BookService bookService;

    @PostMapping("/books")
    public ResponseEntity<Book> createBook(@RequestBody Book book) {
        Book createdBook = bookService.createBook(book);
        return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
    }

    @GetMapping("/books")
    public ResponseEntity<List<Book>> getAllBooks() {
        System.out.println("Endpoint /books foi chamado!");
        List<Book> books = bookService.getAllBook();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/books/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id) {
        Book existBook = bookService.getBookById(id);
        return existBook != null
                ? new ResponseEntity<>(existBook, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/books/category/{id}")
    public ResponseEntity<List<Book>> getBooksByCategoryID(@PathVariable Long id) {
        List<Book> existBooks = bookService.getBooksByCategoryID(id);
        return new ResponseEntity<>(existBooks, HttpStatus.OK);
    }

    @PatchMapping("/updatequantity/{id}")
    public ResponseEntity<Book> patchQuantity(@PathVariable Long id, @RequestBody Book book) {
        Book updated = bookService.patchBookQuantity(id, book);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }
}