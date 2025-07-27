package com.application.bookstore.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class BookRequestValidationTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidBookRequest() {
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle("The Great Gatsby");
        bookRequest.setAuthor("F. Scott Fitzgerald");
        bookRequest.setPublishYear(1925);
        bookRequest.setLanguages(List.of("English"));
        bookRequest.setPublisher("PENGUIN_RANDOM_HOUSE");

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertTrue(violations.isEmpty(), "Valid book request should not have violations");
    }

    @Test
    void testEmptyTitle() {
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle("");
        bookRequest.setAuthor("F. Scott Fitzgerald");
        bookRequest.setPublishYear(1925);
        bookRequest.setLanguages(List.of("English"));
        bookRequest.setPublisher("PENGUIN_RANDOM_HOUSE");

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertEquals(1, violations.size());
        assertEquals("Title is required", violations.iterator().next().getMessage());
    }

    @Test
    void testNullTitle() {
        BookRequest bookRequest = new BookRequest();
        bookRequest.setAuthor("F. Scott Fitzgerald");
        bookRequest.setPublishYear(1925);
        bookRequest.setLanguages(List.of("English"));
        bookRequest.setPublisher("PENGUIN_RANDOM_HOUSE");

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertEquals(1, violations.size());
        assertEquals("Title is required", violations.iterator().next().getMessage());
    }

    @Test
    void testEmptyAuthor() {
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle("The Great Gatsby");
        bookRequest.setAuthor("");
        bookRequest.setPublishYear(1925);
        bookRequest.setLanguages(List.of("English"));
        bookRequest.setPublisher("PENGUIN_RANDOM_HOUSE");

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertEquals(1, violations.size());
        assertEquals("Author is required", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidPublishYearTooLow() {
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle("The Great Gatsby");
        bookRequest.setAuthor("F. Scott Fitzgerald");
        bookRequest.setPublishYear(999);
        bookRequest.setLanguages(List.of("English"));
        bookRequest.setPublisher("PENGUIN_RANDOM_HOUSE");

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertEquals(1, violations.size());
        assertEquals("Publish year must be at least 1000", violations.iterator().next().getMessage());
    }

    @Test
    void testInvalidPublishYearTooHigh() {
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle("The Great Gatsby");
        bookRequest.setAuthor("F. Scott Fitzgerald");
        bookRequest.setPublishYear(2026);
        bookRequest.setLanguages(List.of("English"));
        bookRequest.setPublisher("PENGUIN_RANDOM_HOUSE");

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertEquals(1, violations.size());
        assertEquals("Publish year cannot be greater than 2025", violations.iterator().next().getMessage());
    }

    @Test
    void testEmptyLanguages() {
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle("The Great Gatsby");
        bookRequest.setAuthor("F. Scott Fitzgerald");
        bookRequest.setPublishYear(1925);
        bookRequest.setLanguages(List.of());
        bookRequest.setPublisher("PENGUIN_RANDOM_HOUSE");

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertEquals(1, violations.size());
        assertEquals("At least one language is required", violations.iterator().next().getMessage());
    }

    @Test
    void testNullPublisher() {
        BookRequest bookRequest = new BookRequest();
        bookRequest.setTitle("The Great Gatsby");
        bookRequest.setAuthor("F. Scott Fitzgerald");
        bookRequest.setPublishYear(1925);
        bookRequest.setLanguages(List.of("English"));

        Set<ConstraintViolation<BookRequest>> violations = validator.validate(bookRequest);
        assertEquals(1, violations.size());
        assertEquals("Publisher is required", violations.iterator().next().getMessage());
    }
}
