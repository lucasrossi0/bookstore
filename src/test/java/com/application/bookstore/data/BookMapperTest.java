package com.application.bookstore.data;

import com.application.bookstore.data.entity.Book;
import com.application.bookstore.data.enums.Publisher;
import com.application.bookstore.data.mapper.BookMapper;
import com.application.bookstore.dto.BookRequest;
import com.application.bookstore.dto.BookResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BookMapperTest {

    @Autowired
    private BookMapper bookMapper;

    @Test
    void testMapToEntityValidPublisher() {
        BookRequest request = new BookRequest();
        request.setTitle("The Great Gatsby");
        request.setAuthor("F. Scott Fitzgerald");
        request.setPublishYear(1925);
        request.setLanguages(List.of("English"));
        request.setPublisher("PENGUIN_RANDOM_HOUSE");

        Book book = bookMapper.toEntity(request);

        assertNotNull(book);
        assertEquals("The Great Gatsby", book.getTitle());
        assertEquals("F. Scott Fitzgerald", book.getAuthor());
        assertEquals(1925, book.getPublishYear());
        assertEquals(List.of("English"), book.getLanguages());
        assertEquals(Publisher.PENGUIN_RANDOM_HOUSE, book.getPublisher());
    }

    @Test
    void testMapToEntityInvalidPublisher() {
        BookRequest request = new BookRequest();
        request.setTitle("The Great Gatsby");
        request.setAuthor("F. Scott Fitzgerald");
        request.setPublishYear(1925);
        request.setLanguages(List.of("English"));
        request.setPublisher("INVALID_PUBLISHER");

        assertThrows(IllegalArgumentException.class, () -> bookMapper.toEntity(request));
    }

    @Test
    void testMapToResponse() {
        Book book = new Book(1L, "The Great Gatsby", "F. Scott Fitzgerald", 1925, List.of("English"), Publisher.PENGUIN_RANDOM_HOUSE);

        BookResponse response = bookMapper.toResponse(book);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("The Great Gatsby", response.getTitle());
        assertEquals("F. Scott Fitzgerald", response.getAuthor());
        assertEquals(1925, response.getPublishYear());
        assertEquals(List.of("English"), response.getLanguages());
        assertEquals(Publisher.PENGUIN_RANDOM_HOUSE, response.getPublisher());
    }
}