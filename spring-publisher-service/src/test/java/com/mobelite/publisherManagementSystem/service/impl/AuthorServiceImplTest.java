package com.mobelite.publisherManagementSystem.service.impl;

import com.mobelite.publisherManagementSystem.dto.request.author.AuthorRequestDto;
import com.mobelite.publisherManagementSystem.dto.response.author.AuthorResponseDto;
import com.mobelite.publisherManagementSystem.entity.Author;
import com.mobelite.publisherManagementSystem.entity.Book;
import com.mobelite.publisherManagementSystem.entity.Magazine;
import com.mobelite.publisherManagementSystem.exception.DuplicateResourceException;
import com.mobelite.publisherManagementSystem.exception.ResourceNotFoundException;
import com.mobelite.publisherManagementSystem.mapper.AuthorMapper;
import com.mobelite.publisherManagementSystem.repository.AuthorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthorService Unit Tests")
class AuthorServiceImplTest {

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private AuthorMapper authorMapper;

    @InjectMocks
    private AuthorServiceImpl authorService;

    private Author author;
    private AuthorRequestDto authorRequestDto;
    private AuthorResponseDto authorResponseDto;
    private final Long AUTHOR_ID = 1L;
    private final String AUTHOR_NAME = "John Doe";
    private final String AUTHOR_NATIONALITY = "American";

    @BeforeEach
    void setUp() {
        author = createAuthor();
        authorRequestDto = createAuthorRequestDto();
        authorResponseDto = createAuthorResponseDto();
    }

    @Nested
    @DisplayName("Create Author Tests")
    class CreateAuthorTests {

        @Test
        @DisplayName("Should create author successfully when author name is unique")
        void shouldCreateAuthor_WhenAuthorNameIsUnique() {
            // given
            given(authorRepository.existsByName(AUTHOR_NAME)).willReturn(false);
            given(authorMapper.toEntity(authorRequestDto)).willReturn(author);
            given(authorRepository.save(author)).willReturn(author);
            given(authorMapper.toResponseDto(author)).willReturn(authorResponseDto);

            // when
            AuthorResponseDto result = authorService.createAuthor(authorRequestDto);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo(AUTHOR_NAME);
            assertThat(result.getNationality()).isEqualTo(AUTHOR_NATIONALITY);

            verify(authorRepository).existsByName(AUTHOR_NAME);
            verify(authorMapper).toEntity(authorRequestDto);
            verify(authorRepository).save(author);
            verify(authorMapper).toResponseDto(author);
        }

        @Test
        @DisplayName("Should throw DuplicateResourceException when author name already exists")
        void shouldThrowDuplicateResourceException_WhenAuthorNameAlreadyExists() {
            // given
            given(authorRepository.existsByName(AUTHOR_NAME)).willReturn(true);

            // when & then
            assertThatThrownBy(() -> authorService.createAuthor(authorRequestDto))
                    .isInstanceOf(DuplicateResourceException.class)
                    .hasMessage("Author with name '" + AUTHOR_NAME + "' already exists");

            verify(authorRepository).existsByName(AUTHOR_NAME);
            verify(authorMapper, never()).toEntity(any());
            verify(authorRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get Author Tests")
    class GetAuthorTests {

        @Test
        @DisplayName("Should return author when found by ID")
        void shouldReturnAuthor_WhenFoundById() {
            // given
            given(authorRepository.findByIdWithPublications(AUTHOR_ID)).willReturn(Optional.of(author));
            given(authorMapper.toResponseDto(author)).willReturn(authorResponseDto);

            // when
            AuthorResponseDto result = authorService.getAuthorById(AUTHOR_ID);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo(AUTHOR_NAME);

            verify(authorRepository).findByIdWithPublications(AUTHOR_ID);
            verify(authorMapper).toResponseDto(author);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when author not found by ID")
        void shouldThrowResourceNotFoundException_WhenAuthorNotFoundById() {
            // given
            given(authorRepository.findByIdWithPublications(AUTHOR_ID)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> authorService.getAuthorById(AUTHOR_ID))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Author not found with ID: " + AUTHOR_ID);

            verify(authorRepository).findByIdWithPublications(AUTHOR_ID);
            verify(authorMapper, never()).toResponseDto(any());
        }
    }

    @Nested
    @DisplayName("Get All Authors Tests")
    class GetAllAuthorsTests {

        @Test
        @DisplayName("Should return all authors with their publications")
        void shouldReturnAllAuthors_WithTheirPublications() {
            // given
            List<Author> authors = createAuthorsWithPublications();
            List<AuthorResponseDto> expectedResponses = createAuthorResponseDtos();

            given(authorRepository.findAllWithMagazines()).willReturn(authors);
            given(authorMapper.toResponseDto(authors.get(0))).willReturn(expectedResponses.get(0));
            given(authorMapper.toResponseDto(authors.get(1))).willReturn(expectedResponses.get(1));

            // when
            List<AuthorResponseDto> result = authorService.getAllAuthors();

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getName()).isEqualTo("John Doe");
            assertThat(result.get(1).getName()).isEqualTo("Jane Smith");

            verify(authorRepository).findAllWithMagazines();
            verify(authorMapper, times(2)).toResponseDto(any(Author.class));
        }

        @Test
        @DisplayName("Should return empty list when no authors exist")
        void shouldReturnEmptyList_WhenNoAuthorsExist() {
            // given
            given(authorRepository.findAllWithMagazines()).willReturn(Collections.emptyList());

            // when
            List<AuthorResponseDto> result = authorService.getAllAuthors();

            // then
            assertThat(result).isNotNull();
            assertThat(result).isEmpty();

            verify(authorRepository).findAllWithMagazines();
            verify(authorMapper, never()).toResponseDto(any());
        }

        @Test
        @DisplayName("Should throw RuntimeException when repository throws exception")
        void shouldThrowRuntimeException_WhenRepositoryThrowsException() {
            // given
            given(authorRepository.findAllWithMagazines()).willThrow(new RuntimeException("Database error"));

            // when & then
            assertThatThrownBy(() -> authorService.getAllAuthors())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Failed to fetch authors")
                    .hasCauseInstanceOf(RuntimeException.class);

            verify(authorRepository).findAllWithMagazines();
            verify(authorMapper, never()).toResponseDto(any());
        }

        @Test
        @DisplayName("Should throw RuntimeException when mapper throws exception")
        void shouldThrowRuntimeException_WhenMapperThrowsException() {
            // given
            List<Author> authors = createAuthorsWithPublications();
            given(authorRepository.findAllWithMagazines()).willReturn(authors);
            given(authorMapper.toResponseDto(any(Author.class)))
                    .willThrow(new RuntimeException("Mapping error"));

            // when & then
            assertThatThrownBy(() -> authorService.getAllAuthors())
                    .isInstanceOf(RuntimeException.class)
                    .hasMessage("Failed to fetch authors")
                    .hasCauseInstanceOf(RuntimeException.class);

            verify(authorRepository).findAllWithMagazines();
            verify(authorMapper).toResponseDto(any(Author.class));
        }

        @Test
        @DisplayName("Should handle authors with empty publications")
        void shouldHandleAuthors_WithEmptyPublications() {
            // given
            List<Author> authors = createAuthorsWithEmptyPublications();
            List<AuthorResponseDto> expectedResponses = createAuthorResponseDtos();

            given(authorRepository.findAllWithMagazines()).willReturn(authors);
            given(authorMapper.toResponseDto(authors.get(0))).willReturn(expectedResponses.get(0));

            // when
            List<AuthorResponseDto> result = authorService.getAllAuthors();

            // then
            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getName()).isEqualTo("John Doe");

            verify(authorRepository).findAllWithMagazines();
            verify(authorMapper).toResponseDto(any(Author.class));
        }
    }

    @Nested
    @DisplayName("Exists By ID Tests")
    class ExistsByIdTests {

        @Test
        @DisplayName("Should return true when author exists")
        void shouldReturnTrue_WhenAuthorExists() {
            // given
            given(authorRepository.existsById(AUTHOR_ID)).willReturn(true);

            // when
            boolean result = authorService.existsById(AUTHOR_ID);

            // then
            assertThat(result).isTrue();
            verify(authorRepository).existsById(AUTHOR_ID);
        }

        @Test
        @DisplayName("Should return false when author does not exist")
        void shouldReturnFalse_WhenAuthorDoesNotExist() {
            // given
            given(authorRepository.existsById(AUTHOR_ID)).willReturn(false);

            // when
            boolean result = authorService.existsById(AUTHOR_ID);

            // then
            assertThat(result).isFalse();
            verify(authorRepository).existsById(AUTHOR_ID);
        }
    }

    // Helper methods for creating test data
    private Author createAuthor() {
        Author author = new Author();
        author.setId(AUTHOR_ID);
        author.setName(AUTHOR_NAME);
        author.setNationality(AUTHOR_NATIONALITY);
        author.setBooks(new ArrayList<>());
        author.setMagazines(new HashSet<>());
        return author;
    }

    private AuthorRequestDto createAuthorRequestDto() {
        AuthorRequestDto dto = new AuthorRequestDto();
        dto.setName(AUTHOR_NAME);
        dto.setNationality(AUTHOR_NATIONALITY);
        return dto;
    }

    private AuthorResponseDto createAuthorResponseDto() {
        AuthorResponseDto dto = new AuthorResponseDto();
        dto.setId(AUTHOR_ID);
        dto.setName(AUTHOR_NAME);
        dto.setNationality(AUTHOR_NATIONALITY);
        return dto;
    }

    private List<Author> createAuthorsWithPublications() {
        List<Author> authors = new ArrayList<>();

        // First author with books and magazines
        Author author1 = new Author();
        author1.setId(1L);
        author1.setName("John Doe");
        author1.setNationality("American");

        List<Book> books1 = new ArrayList<>();
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("Test Book");
        books1.add(book1);
        author1.setBooks(books1);

        Set<Magazine> magazines1 = new HashSet<>();
        Magazine magazine1 = new Magazine();
        magazine1.setId(1L);
        magazine1.setTitle("Test Magazine");
        magazines1.add(magazine1);
        author1.setMagazines(magazines1);

        authors.add(author1);

        // Second author with different publications
        Author author2 = new Author();
        author2.setId(2L);
        author2.setName("Jane Smith");
        author2.setNationality("British");

        List<Book> books2 = new ArrayList<>();
        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("Another Book");
        books2.add(book2);
        author2.setBooks(books2);

        Set<Magazine> magazines2 = new HashSet<>();
        Magazine magazine2 = new Magazine();
        magazine2.setId(2L);
        magazine2.setTitle("Another Magazine");
        magazines2.add(magazine2);
        author2.setMagazines(magazines2);

        authors.add(author2);

        return authors;
    }

    private List<Author> createAuthorsWithEmptyPublications() {
        List<Author> authors = new ArrayList<>();

        Author author = new Author();
        author.setId(1L);
        author.setName("John Doe");
        author.setNationality("American");
        author.setBooks(new ArrayList<>());
        author.setMagazines(new HashSet<>());

        authors.add(author);
        return authors;
    }

    private List<AuthorResponseDto> createAuthorResponseDtos() {
        List<AuthorResponseDto> responses = new ArrayList<>();

        AuthorResponseDto response1 = new AuthorResponseDto();
        response1.setId(1L);
        response1.setName("John Doe");
        response1.setNationality("American");
        responses.add(response1);

        AuthorResponseDto response2 = new AuthorResponseDto();
        response2.setId(2L);
        response2.setName("Jane Smith");
        response2.setNationality("British");
        responses.add(response2);

        return responses;
    }
}