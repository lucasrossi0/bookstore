package com.application.bookstore.service;

import com.application.bookstore.data.entity.Book;
import com.application.bookstore.data.enums.Publisher;
import com.application.bookstore.data.mapper.BookMapper;
import com.application.bookstore.data.repository.BookRepository;
import com.application.bookstore.dto.BookRequest;
import com.application.bookstore.exception.BookNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookService bookService;

    private Book book;
    private BookRequest bookRequest;

    @BeforeEach
    void setUp() {
        book = new Book(1L, "The Great Gatsby", "F. Scott Fitzgerald", 1925, List.of("English"), Publisher.PENGUIN_RANDOM_HOUSE);
        bookRequest = new BookRequest();
        bookRequest.setTitle("The Great Gatsby");
        bookRequest.setAuthor("F. Scott Fitzgerald");
        bookRequest.setPublishYear(1925);
        bookRequest.setLanguages(List.of("English"));
        bookRequest.setPublisher("PENGUIN_RANDOM_HOUSE");
    }

    @Test
    void testCreateBookSuccess() {
        when(bookMapper.toEntity(any(BookRequest.class))).thenReturn(book);
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book createdBook = bookService.createBook(bookRequest);

        assertNotNull(createdBook);
        assertEquals("The Great Gatsby", createdBook.getTitle());
        assertEquals(Publisher.PENGUIN_RANDOM_HOUSE, createdBook.getPublisher());
        verify(bookMapper, times(1)).toEntity(bookRequest);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testGetBooksSuccess() {
        List<Book> books = Arrays.asList(book);
        when(bookRepository.findAll()).thenReturn(books);

        List<Book> result = bookService.getBooks();

        assertEquals(1, result.size());
        assertEquals("The Great Gatsby", result.getFirst().getTitle());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testGetBooksEmpty() {
        when(bookRepository.findAll()).thenReturn(List.of());

        List<Book> result = bookService.getBooks();

        assertTrue(result.isEmpty());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testGetBookByIdSuccess() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        Book result = bookService.getBooksById(1L);

        assertNotNull(result);
        assertEquals("The Great Gatsby", result.getTitle());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void testGetBookByIdNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> bookService.getBooksById(1L));
        assertEquals("Book with id: 1 not found", exception.getMessage());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdateBookSuccess() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        Book updatedBook = bookService.updateBook(1L, bookRequest);

        assertNotNull(updatedBook);
        assertEquals("The Great Gatsby", updatedBook.getTitle());
        verify(bookMapper, times(1)).updateBookFromRequest(bookRequest, book);
        verify(bookRepository, times(1)).save(book);
    }

    @Test
    void testUpdateBookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> bookService.updateBook(1L, bookRequest));
        assertEquals("Book with id: 1 not found", exception.getMessage());
        verify(bookRepository, times(1)).findById(1L);
        verify(bookMapper, never()).updateBookFromRequest(any(), any());
    }

    @Test
    void testDeleteBookSuccess() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.deleteBook(1L);

        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, times(1)).delete(book);
    }

    @Test
    void testDeleteBookNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        BookNotFoundException exception = assertThrows(BookNotFoundException.class, () -> bookService.deleteBook(1L));
        assertEquals("Book with id: 1 not found", exception.getMessage());
        verify(bookRepository, times(1)).findById(1L);
        verify(bookRepository, never()).delete(any());
    }
}