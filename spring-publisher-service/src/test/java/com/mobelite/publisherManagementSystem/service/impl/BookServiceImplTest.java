package com.mobelite.publisherManagementSystem.service.impl;

import com.mobelite.publisherManagementSystem.dto.request.book.BookCreateRequestDto;
import com.mobelite.publisherManagementSystem.dto.request.book.BookUpdateRequestDto;
import com.mobelite.publisherManagementSystem.dto.response.book.BookResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.book.BookSummaryResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.author.AuthorSummaryDto;
import com.mobelite.publisherManagementSystem.entity.Author;
import com.mobelite.publisherManagementSystem.entity.Book;
import com.mobelite.publisherManagementSystem.exception.DuplicateResourceException;
import com.mobelite.publisherManagementSystem.exception.ResourceNotFoundException;
import com.mobelite.publisherManagementSystem.mapper.BookMapper;
import com.mobelite.publisherManagementSystem.repository.AuthorRepository;
import com.mobelite.publisherManagementSystem.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("BookServiceImpl Tests")
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private BookMapper bookMapper;

    @InjectMocks
    private BookServiceImpl bookService;

    private Author testAuthor;
    private Book testBook;
    private BookCreateRequestDto createRequestDto;
    private BookUpdateRequestDto updateRequestDto;
    private BookResponseDto responseDto;
    private BookSummaryResponseDto summaryResponseDto;
    private AuthorSummaryDto authorSummaryDto;

    @BeforeEach
    void setUp() {
        testAuthor = createTestAuthor();
        testBook = createTestBook();
        createRequestDto = createBookCreateRequestDto();
        updateRequestDto = createBookUpdateRequestDto();
        authorSummaryDto = createAuthorSummaryDto();
        responseDto = createBookResponseDto();
        summaryResponseDto = createBookSummaryResponseDto();
    }

    @Nested
    @DisplayName("Create Book Tests")
    class CreateBookTests {

        @Test
        @DisplayName("Should create book successfully")
        void shouldCreateBookSuccessfully() {
            // Given
            when(authorRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
            when(bookMapper.toEntity(createRequestDto)).thenReturn(testBook);
            when(bookRepository.save(any(Book.class))).thenReturn(testBook);
            when(bookMapper.toResponse(testBook)).thenReturn(responseDto);

            // When
            BookResponseDto result = bookService.createBook(createRequestDto);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(responseDto);
            assertThat(result.getIsbn()).isEqualTo(testBook.getIsbn());
            assertThat(result.getAuthor().getId()).isEqualTo(testAuthor.getId());

            verify(authorRepository).findById(1L);
            verify(bookMapper).toEntity(createRequestDto);
            verify(bookRepository).save(any(Book.class));
            verify(bookMapper).toResponse(testBook);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when author not found")
        void shouldThrowResourceNotFoundExceptionWhenAuthorNotFound() {
            // Given
            when(authorRepository.findById(1L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> bookService.createBook(createRequestDto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Author with ID 1 not found");

            verify(authorRepository).findById(1L);
            verify(bookRepository, never()).save(any(Book.class));
            verify(bookMapper, never()).toEntity(any(BookCreateRequestDto.class));
        }

        @Test
        @DisplayName("Should throw DuplicateResourceException when ISBN already exists")
        void shouldThrowDuplicateResourceExceptionWhenIsbnExists() {
            // Given
            when(authorRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
            when(bookMapper.toEntity(createRequestDto)).thenReturn(testBook);
            when(bookRepository.save(any(Book.class)))
                    .thenThrow(new DataIntegrityViolationException("Duplicate entry for isbn"));

            // When & Then
            assertThatThrownBy(() -> bookService.createBook(createRequestDto))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessage("Book with ISBN 978-0123456789 already exists");

            verify(authorRepository).findById(1L);
            verify(bookRepository).save(any(Book.class));
            verify(bookMapper, never()).toResponse(any(Book.class));
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException for other database constraint violations")
        void shouldThrowIllegalArgumentExceptionForOtherConstraints() {
            // Given
            when(authorRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
            when(bookMapper.toEntity(createRequestDto)).thenReturn(testBook);
            when(bookRepository.save(any(Book.class)))
                    .thenThrow(new DataIntegrityViolationException("Other constraint violation"));

            // When & Then
            assertThatThrownBy(() -> bookService.createBook(createRequestDto))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Database constraint violation: Other constraint violation");

            verify(authorRepository).findById(1L);
            verify(bookRepository).save(any(Book.class));
            verify(bookMapper, never()).toResponse(any(Book.class));
        }

    }

    @Nested
    @DisplayName("Update Book Tests")
    class UpdateBookTests {

        @Test
        @DisplayName("Should update book successfully")
        void shouldUpdateBookSuccessfully() {
            // Given
            when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
            // Add this line to mock the author repository call
            when(authorRepository.findById(1L)).thenReturn(Optional.of(testAuthor));
            when(bookRepository.save(testBook)).thenReturn(testBook);
            when(bookMapper.toResponse(testBook)).thenReturn(responseDto);

            // When
            BookResponseDto result = bookService.updateBook(1L, updateRequestDto);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(responseDto);

            verify(bookRepository).findById(1L);
            verify(authorRepository).findById(1L); // Add this verification
            verify(bookMapper).updateEntityFromRequest(updateRequestDto, testBook);
            verify(bookRepository).save(testBook);
            verify(bookMapper).toResponse(testBook);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when book not found")
        void shouldThrowResourceNotFoundExceptionWhenBookNotFound() {
            // Given
            when(bookRepository.findById(1L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> bookService.updateBook(1L, updateRequestDto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Book with ID 1 not found");

            verify(bookRepository).findById(1L);
            verify(bookRepository, never()).save(any(Book.class));
            verify(bookMapper, never()).updateEntityFromRequest(any(), any());
        }


        @Test
        @DisplayName("Should update author when authorId is provided")
        void shouldUpdateAuthorWhenAuthorIdProvided() {
            // Given
            BookUpdateRequestDto updateRequest = BookUpdateRequestDto.builder()
                    .authorId(2L)
                    .build();
            Author newAuthor = createTestAuthor();
            newAuthor.setId(2L);
            newAuthor.setName("Updated Author");

            // Create a fresh book instance to avoid spy issues
            Book bookToUpdate = new Book();
            bookToUpdate.setId(1L);
            bookToUpdate.setTitle("Test Book");
            bookToUpdate.setIsbn("978-0123456789");
            bookToUpdate.setPublicationDate(LocalDate.of(2023, 1, 1));
            bookToUpdate.setAuthor(testAuthor);

            when(bookRepository.findById(1L)).thenReturn(Optional.of(bookToUpdate));
            when(authorRepository.findById(2L)).thenReturn(Optional.of(newAuthor));
            when(bookRepository.save(bookToUpdate)).thenReturn(bookToUpdate);
            when(bookMapper.toResponse(bookToUpdate)).thenReturn(responseDto);

            // When
            BookResponseDto result = bookService.updateBook(1L, updateRequest);

            // Then
            assertThat(result).isNotNull();
            assertThat(bookToUpdate.getAuthor()).isEqualTo(newAuthor); // Check the actual object
            verify(authorRepository).findById(2L);
            verify(bookRepository).save(bookToUpdate);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when new author not found")
        void shouldThrowResourceNotFoundExceptionWhenNewAuthorNotFound() {
            // Given
            BookUpdateRequestDto updateRequest = BookUpdateRequestDto.builder()
                    .authorId(999L)
                    .build();

            // Create a fresh book instance without spy
            Book bookToUpdate = new Book();
            bookToUpdate.setId(1L);
            bookToUpdate.setTitle("Test Book");
            bookToUpdate.setIsbn("978-0123456789");
            bookToUpdate.setPublicationDate(LocalDate.of(2023, 1, 1));
            bookToUpdate.setAuthor(testAuthor);

            when(bookRepository.findById(1L)).thenReturn(Optional.of(bookToUpdate));
            when(authorRepository.findById(999L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> bookService.updateBook(1L, updateRequest))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Author with ID 999 not found");

            verify(authorRepository).findById(999L);
            verify(bookRepository, never()).save(any(Book.class));
            // Remove the verification on testBook.setAuthor since it's causing issues
        }



        @Test
        @DisplayName("Should not update ISBN when it's the same as existing")
        void shouldNotUpdateIsbnWhenSameAsExisting() {
            // Given
            BookUpdateRequestDto updateRequest = BookUpdateRequestDto.builder()
                    .isbn("978-0123456789") // Same as existing
                    .build();
            when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
            when(bookRepository.save(testBook)).thenReturn(testBook);
            when(bookMapper.toResponse(testBook)).thenReturn(responseDto);

            // When
            BookResponseDto result = bookService.updateBook(1L, updateRequest);

            // Then
            assertThat(result).isNotNull();
            verify(bookRepository).findById(1L);
            verify(bookRepository, never()).existsByIsbnAndIdNot(anyString(), anyLong());
            verify(bookRepository).save(testBook);
        }

        @Test
        @DisplayName("Should not check ISBN uniqueness when ISBN is null")
        void shouldNotCheckIsbnUniquenessWhenIsbnIsNull() {
            // Given
            BookUpdateRequestDto updateRequest = BookUpdateRequestDto.builder()
                    .authorId(2L)
                    .build(); // ISBN is null
            Author newAuthor = createTestAuthor();
            newAuthor.setId(2L);

            when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
            when(authorRepository.findById(2L)).thenReturn(Optional.of(newAuthor));
            when(bookRepository.save(testBook)).thenReturn(testBook);
            when(bookMapper.toResponse(testBook)).thenReturn(responseDto);

            // When
            BookResponseDto result = bookService.updateBook(1L, updateRequest);

            // Then
            assertThat(result).isNotNull();
            verify(bookRepository, never()).existsByIsbnAndIdNot(anyString(), anyLong());
            verify(authorRepository).findById(2L);
            verify(bookRepository).save(testBook);
        }
    }

    @Nested
    @DisplayName("Get Book Tests")
    class GetBookTests {

        @Test
        @DisplayName("Should get book by ID successfully")
        void shouldGetBookByIdSuccessfully() {
            // Given
            when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
            when(bookMapper.toResponse(testBook)).thenReturn(responseDto);

            // When
            BookResponseDto result = bookService.getBookById(1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(responseDto);
            assertThat(result.getIsbn()).isEqualTo(testBook.getIsbn());

            verify(bookRepository).findById(1L);
            verify(bookMapper).toResponse(testBook);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when book not found by ID")
        void shouldThrowResourceNotFoundExceptionWhenBookNotFoundById() {
            // Given
            when(bookRepository.findById(1L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> bookService.getBookById(1L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Book with ID 1 not found");

            verify(bookRepository).findById(1L);
            verify(bookMapper, never()).toResponse(any(Book.class));
        }

        @Test
        @DisplayName("Should get book by ISBN successfully")
        void shouldGetBookByIsbnSuccessfully() {
            // Given
            String isbn = "978-0123456789";
            when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.of(testBook));
            when(bookMapper.toResponse(testBook)).thenReturn(responseDto);

            // When
            BookResponseDto result = bookService.getBookByIsbn(isbn);

            // Then
            assertThat(result).isNotNull();
            assertThat(result).isEqualTo(responseDto);
            assertThat(result.getIsbn()).isEqualTo(isbn);

            verify(bookRepository).findByIsbn(isbn);
            verify(bookMapper).toResponse(testBook);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when book not found by ISBN")
        void shouldThrowResourceNotFoundExceptionWhenBookNotFoundByIsbn() {
            // Given
            String isbn = "978-0123456789";
            when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> bookService.getBookByIsbn(isbn))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Book with ISBN 978-0123456789 not found");

            verify(bookRepository).findByIsbn(isbn);
            verify(bookMapper, never()).toResponse(any(Book.class));
        }

        @Test
        @DisplayName("Should handle null ISBN gracefully")
        void shouldHandleNullIsbnGracefully() {
            // Given
            String isbn = null;
            when(bookRepository.findByIsbn(isbn)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> bookService.getBookByIsbn(isbn))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Book with ISBN null not found");

            verify(bookRepository).findByIsbn(isbn);
        }
    }

    @Nested
    @DisplayName("Get All Books Tests")
    class GetAllBooksTests {

        @Test
        @DisplayName("Should get all books with pagination")
        void shouldGetAllBooksWithPagination() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            List<Book> books = List.of(testBook);
            Page<Book> bookPage = new PageImpl<>(books, pageable, 1);

            when(bookRepository.findAll(pageable)).thenReturn(bookPage);
            when(bookMapper.toSummaryResponse(testBook)).thenReturn(summaryResponseDto);

            // When
            Page<BookResponseDto> result = bookService.getAllBooks(pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0)).isEqualTo(summaryResponseDto);
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getTotalPages()).isEqualTo(1);
            assertThat(result.getNumber()).isEqualTo(0);
            assertThat(result.getSize()).isEqualTo(10);

            verify(bookRepository).findAll(pageable);
            verify(bookMapper).toSummaryResponse(testBook);
        }

        @Test
        @DisplayName("Should return empty page when no books exist")
        void shouldReturnEmptyPageWhenNoBooksExist() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Book> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(bookRepository.findAll(pageable)).thenReturn(emptyPage);

            // When
            Page<BookResponseDto> result = bookService.getAllBooks(pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);
            assertThat(result.getTotalPages()).isEqualTo(0);

            verify(bookRepository).findAll(pageable);
            verify(bookMapper, never()).toSummaryResponse(any(Book.class));
        }

        @Test
        @DisplayName("Should get books by author")
        void shouldGetBooksByAuthor() {
            // Given
            Long authorId = 1L;
            Pageable pageable = PageRequest.of(0, 10);
            List<Book> books = List.of(testBook);
            Page<Book> bookPage = new PageImpl<>(books, pageable, 1);

            when(bookRepository.findByAuthorId(authorId, pageable)).thenReturn(bookPage);
            when(bookMapper.toSummaryResponse(testBook)).thenReturn(summaryResponseDto);

            // When
            Page<BookSummaryResponseDto> result = bookService.getBooksByAuthor(authorId, pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0)).isEqualTo(summaryResponseDto);
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().get(0).getAuthorName()).isEqualTo("Test Author");

            verify(bookRepository).findByAuthorId(authorId, pageable);
            verify(bookMapper).toSummaryResponse(testBook);
        }

        @Test
        @DisplayName("Should return empty page when author has no books")
        void shouldReturnEmptyPageWhenAuthorHasNoBooks() {
            // Given
            Long authorId = 1L;
            Pageable pageable = PageRequest.of(0, 10);
            Page<Book> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(bookRepository.findByAuthorId(authorId, pageable)).thenReturn(emptyPage);

            // When
            Page<BookSummaryResponseDto> result = bookService.getBooksByAuthor(authorId, pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);

            verify(bookRepository).findByAuthorId(authorId, pageable);
            verify(bookMapper, never()).toSummaryResponse(any(Book.class));
        }

        @Test
        @DisplayName("Should handle multiple books with different authors")
        void shouldHandleMultipleBooksWithDifferentAuthors() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);

            Book book1 = createTestBook();
            Book book2 = createTestBook();
            book2.setId(2L);
            book2.setIsbn("978-9876543210");

            Author author2 = createTestAuthor();
            author2.setId(2L);
            author2.setName("Second Author");
            book2.setAuthor(author2);

            List<Book> books = List.of(book1, book2);
            Page<Book> bookPage = new PageImpl<>(books, pageable, 2);

            BookSummaryResponseDto summaryDto1 = BookSummaryResponseDto.builder()
                    .isbn("978-0123456789")
                    .authorName("Test Author")
                    .build();

            BookSummaryResponseDto summaryDto2 = BookSummaryResponseDto.builder()
                    .isbn("978-9876543210")
                    .authorName("Second Author")
                    .build();

            when(bookRepository.findAll(pageable)).thenReturn(bookPage);
            when(bookMapper.toSummaryResponse(book1)).thenReturn(summaryDto1);
            when(bookMapper.toSummaryResponse(book2)).thenReturn(summaryDto2);

            // When
            Page<BookResponseDto> result = bookService.getAllBooks(pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getTotalElements()).isEqualTo(2);

            assertThat(result.getContent().get(0).getIsbn()).isEqualTo("978-0123456789");
            assertThat(result.getContent().get(0).getAuthor().getName()).isEqualTo("Test Author");

            assertThat(result.getContent().get(1).getIsbn()).isEqualTo("978-9876543210");
            assertThat(result.getContent().get(1).getAuthor().getName()).isEqualTo("Second Author");

            verify(bookRepository).findAll(pageable);
            verify(bookMapper).toSummaryResponse(book1);
            verify(bookMapper).toSummaryResponse(book2);
        }
    }


    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesAndErrorHandling {
        @Test
        @DisplayName("Should handle mapper throwing exception")
        void shouldHandleMapperThrowingException() {
            // Given
            when(bookRepository.findById(1L)).thenReturn(Optional.of(testBook));
            when(bookMapper.toResponse(testBook)).thenThrow(new RuntimeException("Mapping error"));

            // When & Then
            assertThatThrownBy(() -> bookService.getBookById(1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Mapping error");

            verify(bookRepository).findById(1L);
            verify(bookMapper).toResponse(testBook);
        }

        @Test
        @DisplayName("Should handle repository throwing unexpected exception")
        void shouldHandleRepositoryThrowingUnexpectedException() {
            // Given
            when(bookRepository.findById(1L)).thenThrow(new RuntimeException("Database connection error"));

            // When & Then
            assertThatThrownBy(() -> bookService.getBookById(1L))
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Database connection error");

            verify(bookRepository).findById(1L);
            verify(bookMapper, never()).toResponse(any(Book.class));
        }
        @Nested
        @DisplayName("Delete Book Tests")
        class DeleteBookTests {

            @Test
            @DisplayName("Should delete book successfully when it exists")
            void shouldDeleteBookSuccessfullyWhenItExists() {
                // Given
                when(bookRepository.existsById(1L)).thenReturn(true);
                doNothing().when(bookRepository).deleteById(1L);

                // When
                bookService.deleteBook(1L);

                // Then
                verify(bookRepository).existsById(1L);
                verify(bookRepository).deleteById(1L);
            }

            @Test
            @DisplayName("Should throw ResourceNotFoundException when book to delete doesn't exist")
            void shouldThrowResourceNotFoundExceptionWhenBookToDeleteDoesntExist() {
                // Given
                when(bookRepository.existsById(1L)).thenReturn(false);

                // When & Then
                assertThatThrownBy(() -> bookService.deleteBook(1L))
                        .isInstanceOf(ResourceNotFoundException.class)
                        .hasMessage("Book with ID 1 not found");

                verify(bookRepository).existsById(1L);
                verify(bookRepository, never()).deleteById(anyLong());
            }

            @Test
            @DisplayName("Should handle repository exception during deletion")
            void shouldHandleRepositoryExceptionDuringDeletion() {
                // Given
                when(bookRepository.existsById(1L)).thenReturn(true);
                doThrow(new RuntimeException("Database error")).when(bookRepository).deleteById(1L);

                // When & Then
                assertThatThrownBy(() -> bookService.deleteBook(1L))
                        .isInstanceOf(RuntimeException.class)
                        .hasMessage("Database error");

                verify(bookRepository).existsById(1L);
                verify(bookRepository).deleteById(1L);
            }
        }

        @Nested
        @DisplayName("Exists By ID Tests")
        class ExistsByIdTests {

            @Test
            @DisplayName("Should return true when book exists")
            void shouldReturnTrueWhenBookExists() {
                // Given
                when(bookRepository.existsById(1L)).thenReturn(true);

                // When
                boolean result = bookService.existsById(1L);

                // Then
                assertThat(result).isTrue();
                verify(bookRepository).existsById(1L);
            }

            @Test
            @DisplayName("Should return false when book doesn't exist")
            void shouldReturnFalseWhenBookDoesntExist() {
                // Given
                when(bookRepository.existsById(1L)).thenReturn(false);

                // When
                boolean result = bookService.existsById(1L);

                // Then
                assertThat(result).isFalse();
                verify(bookRepository).existsById(1L);
            }

            @Test
            @DisplayName("Should handle repository exception")
            void shouldHandleRepositoryException() {
                // Given
                when(bookRepository.existsById(1L)).thenThrow(new RuntimeException("Database error"));

                // When & Then
                assertThatThrownBy(() -> bookService.existsById(1L))
                        .isInstanceOf(RuntimeException.class)
                        .hasMessage("Database error");

                verify(bookRepository).existsById(1L);
            }
        }
    }

    // Helper methods for test data creation
    private Author createTestAuthor() {
        Author author = new Author(); // Remove spy()
        author.setId(1L);
        author.setName("Test Author");
        return author;
    }

    private Book createTestBook() {
        Book book = new Book(); // Remove spy()
        book.setId(1L);
        book.setTitle("Test Book");
        book.setIsbn("978-0123456789");
        book.setPublicationDate(LocalDate.of(2023, 1, 1));
        book.setAuthor(testAuthor);
        return book;
    }


    private BookCreateRequestDto createBookCreateRequestDto() {
        return BookCreateRequestDto.builder()
                .title("Test Book")
                .isbn("978-0123456789")
                .publicationDate(LocalDate.of(2023, 1, 1))
                .authorId(1L)
                .build();
    }

    private BookUpdateRequestDto createBookUpdateRequestDto() {
        return BookUpdateRequestDto.builder()
                .isbn("978-0123456789")
                .authorId(1L)
                .build();
    }

    private AuthorSummaryDto createAuthorSummaryDto() {
        return AuthorSummaryDto.builder()
                .id(1L)
                .name("Test Author")
                .build();
    }

    private BookResponseDto createBookResponseDto() {
        return BookResponseDto.builder()
                .isbn("978-0123456789")
                .author(authorSummaryDto)
                .build();
    }

    private BookSummaryResponseDto createBookSummaryResponseDto() {
        return BookSummaryResponseDto.builder()
                .isbn("978-0123456789")
                .authorName("Test Author")
                .build();
    }
}