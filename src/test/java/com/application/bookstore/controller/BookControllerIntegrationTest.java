package com.application.bookstore.controller;

import com.application.bookstore.data.entity.Book;
import com.application.bookstore.data.enums.Publisher;
import com.application.bookstore.data.repository.BookRepository;
import com.application.bookstore.dto.BookRequest;
import com.application.bookstore.dto.BookResponse;
import com.application.bookstore.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        bookRepository.deleteAll();
    }

    @Test
    void testCreateBookSuccess() {
        BookRequest request = new BookRequest();
        request.setTitle("The Great Gatsby");
        request.setAuthor("F. Scott Fitzgerald");
        request.setPublishYear(1925);
        request.setLanguages(List.of("English"));
        request.setPublisher("PENGUIN_RANDOM_HOUSE");

        ResponseEntity<BookResponse> response = restTemplate.postForEntity("/api/books", request, BookResponse.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("The Great Gatsby", response.getBody().getTitle());
        assertEquals(Publisher.PENGUIN_RANDOM_HOUSE, response.getBody().getPublisher());
    }

    @Test
    void testCreateBookInvalidInput() {
        BookRequest request = new BookRequest();
        request.setTitle("");
        request.setAuthor("F. Scott Fitzgerald");
        request.setPublishYear(1925);
        request.setLanguages(List.of("English"));
        request.setPublisher("PENGUIN_RANDOM_HOUSE");

        ResponseEntity<ErrorResponse> response = restTemplate.postForEntity("/api/books", request, ErrorResponse.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getDetails().contains("Title is required"));
    }

    @Test
    void testGetBooksSuccess() {
        Book book = new Book(null, "The Great Gatsby", "F. Scott Fitzgerald", 1925, List.of("English"), Publisher.PENGUIN_RANDOM_HOUSE);
        bookRepository.save(book);

        ResponseEntity<BookResponse[]> response = restTemplate.getForEntity("/api/books", BookResponse[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().length);
        assertEquals("The Great Gatsby", response.getBody()[0].getTitle());
    }

    @Test
    void testGetBooksEmpty() {
        ResponseEntity<BookResponse[]> response = restTemplate.getForEntity("/api/books", BookResponse[].class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testGetBookByIdSuccess() {
        Book book = new Book(null, "The Great Gatsby", "F. Scott Fitzgerald", 1925, List.of("English"), Publisher.PENGUIN_RANDOM_HOUSE);
        book = bookRepository.save(book);

        ResponseEntity<BookResponse> response = restTemplate.getForEntity("/api/books/" + book.getId(), BookResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("The Great Gatsby", response.getBody().getTitle());
    }

    @Test
    void testGetBookByIdNotFound() {
        ResponseEntity<ErrorResponse> response = restTemplate.getForEntity("/api/books/999", ErrorResponse.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Book not found", response.getBody().getMessage());
    }

    @Test
    void testUpdateBookSuccess() {
        Book book = new Book(null, "The Great Gatsby", "F. Scott Fitzgerald", 1925, List.of("English"), Publisher.PENGUIN_RANDOM_HOUSE);
        book = bookRepository.save(book);

        BookRequest request = new BookRequest();
        request.setTitle("Updated Title");
        request.setAuthor("F. Scott Fitzgerald");
        request.setPublishYear(1925);
        request.setLanguages(List.of("English"));
        request.setPublisher("PENGUIN_RANDOM_HOUSE");

        HttpEntity<BookRequest> entity = new HttpEntity<>(request);
        ResponseEntity<BookResponse> response = restTemplate.exchange("/api/books/" + book.getId(), HttpMethod.PUT, entity, BookResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Title", response.getBody().getTitle());
    }

    @Test
    void testUpdateBookNotFound() {
        BookRequest request = new BookRequest();
        request.setTitle("Updated Title");
        request.setAuthor("F. Scott Fitzgerald");
        request.setPublishYear(1925);
        request.setLanguages(List.of("English"));
        request.setPublisher("PENGUIN_RANDOM_HOUSE");

        HttpEntity<BookRequest> entity = new HttpEntity<>(request);
        ResponseEntity<ErrorResponse> response = restTemplate.exchange("/api/books/999", HttpMethod.PUT, entity, ErrorResponse.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Book not found", response.getBody().getMessage());
    }

    @Test
    void testDeleteBookSuccess() {
        Book book = new Book(null, "The Great Gatsby", "F. Scott Fitzgerald", 1925, List.of("English"), Publisher.PENGUIN_RANDOM_HOUSE);
        book = bookRepository.save(book);

        ResponseEntity<Void> response = restTemplate.exchange("/api/books/" + book.getId(), HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(bookRepository.findById(book.getId()).isPresent());
    }

    @Test
    void testDeleteBookNotFound() {
        ResponseEntity<ErrorResponse> response = restTemplate.exchange("/api/books/999", HttpMethod.DELETE, null, ErrorResponse.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Book not found", response.getBody().getMessage());
    }
}