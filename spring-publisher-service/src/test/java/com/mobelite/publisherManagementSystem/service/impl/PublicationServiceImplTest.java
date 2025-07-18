package com.mobelite.publisherManagementSystem.service.impl;

import com.mobelite.publisherManagementSystem.dto.response.book.BookSummaryResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.magazine.MagazineSummaryResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.publication.GroupedPublicationsResponse;
import com.mobelite.publisherManagementSystem.dto.response.publication.PublicationResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.publication.PublicationSummaryResponseDto;
import com.mobelite.publisherManagementSystem.entity.Author;
import com.mobelite.publisherManagementSystem.entity.Book;
import com.mobelite.publisherManagementSystem.entity.Magazine;
import com.mobelite.publisherManagementSystem.entity.Publication;
import com.mobelite.publisherManagementSystem.exception.ResourceNotFoundException;
import com.mobelite.publisherManagementSystem.mapper.PublicationMapper;
import com.mobelite.publisherManagementSystem.repository.AuthorRepository;
import com.mobelite.publisherManagementSystem.repository.PublicationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PublicationServiceImpl Unit Tests")
class PublicationServiceImplTest {

    // Concrete test implementation of the abstract Publication class
    private static class TestPublication extends Publication {
        // No additional implementation needed for basic testing
    }

    @Mock
    private AuthorRepository authorRepository;

    @Mock
    private PublicationRepository publicationRepository;

    @Mock
    private PublicationMapper publicationMapper;

    @InjectMocks
    private PublicationServiceImpl publicationService;

    private Author testAuthor;
    private Publication testPublication;
    private PublicationResponseDto responseDto;
    private PublicationSummaryResponseDto summaryResponseDto;

    @BeforeEach
    void setUp() {
        // Test Author
        testAuthor = new Author();
        testAuthor.setId(1L);
        testAuthor.setName("Test Author");

        // Test Publication - using concrete subclass
        testPublication = new TestPublication();
        testPublication.setId(1L);
        testPublication.setTitle("Test Publication");
        testPublication.setPublicationDate(LocalDate.of(2023, 1, 1));

        // Response DTO
        responseDto = new PublicationResponseDto();
        responseDto.setId(1L);
        responseDto.setTitle("Test Publication");
        responseDto.setPublicationDate(LocalDate.of(2023, 1, 1));

        // Summary Response DTO
        summaryResponseDto = new PublicationSummaryResponseDto();
        summaryResponseDto.setId(1L);
        summaryResponseDto.setTitle("Test Publication");
    }


    @Nested
    @DisplayName("Get Publication Tests")
    class GetPublicationTests {

        @Test
        @DisplayName("Should get publication by ID successfully")
        void shouldGetPublicationByIdSuccessfully() {
            // Given
            when(publicationRepository.findById(1L)).thenReturn(Optional.of(testPublication));
            when(publicationMapper.toResponseDto(testPublication)).thenReturn(responseDto);

            // When
            PublicationResponseDto result = publicationService.getPublicationById(1L);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(1L);
            assertThat(result.getTitle()).isEqualTo("Test Publication");

            verify(publicationRepository).findById(1L);
            verify(publicationMapper).toResponseDto(testPublication);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when publication not found")
        void shouldThrowResourceNotFoundExceptionWhenPublicationNotFound() {
            // Given
            when(publicationRepository.findById(1L)).thenReturn(Optional.empty());

            // When & Then
            assertThatThrownBy(() -> publicationService.getPublicationById(1L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Publication not found with ID: 1");

            verify(publicationRepository).findById(1L);
            verifyNoInteractions(publicationMapper);
        }
    }

    @Nested
    @DisplayName("Get All Publications Tests")
    class GetAllPublicationsTests {

        @Test
        @DisplayName("Should get all publications with pagination")
        void shouldGetAllPublicationsWithPagination() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            List<Publication> publications = Arrays.asList(testPublication);
            Page<Publication> publicationPage = new PageImpl<>(publications, pageable, 1);

            when(publicationRepository.findAll(pageable)).thenReturn(publicationPage);
            when(publicationMapper.toSummaryResponseDto(testPublication)).thenReturn(summaryResponseDto);

            // When
            Page<PublicationSummaryResponseDto> result = publicationService.getAllPublications(pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(1L);
            assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Publication");

            verify(publicationRepository).findAll(pageable);
            verify(publicationMapper).toSummaryResponseDto(testPublication);
        }

        @Test
        @DisplayName("Should return empty page when no publications exist")
        void shouldReturnEmptyPageWhenNoPublicationsExist() {
            // Given
            Pageable pageable = PageRequest.of(0, 10);
            Page<Publication> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(publicationRepository.findAll(pageable)).thenReturn(emptyPage);

            // When
            Page<PublicationSummaryResponseDto> result = publicationService.getAllPublications(pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);

            verify(publicationRepository).findAll(pageable);
            verifyNoInteractions(publicationMapper);
        }
    }

    @Nested
    @DisplayName("Search Publications Tests")
    class SearchPublicationsTests {

        @Test
        @DisplayName("Should search publications by title")
        void shouldSearchPublicationsByTitle() {
            // Given
            String searchTitle = "Test";
            Pageable pageable = PageRequest.of(0, 10);
            List<Publication> publications = Arrays.asList(testPublication);
            Page<Publication> publicationPage = new PageImpl<>(publications, pageable, 1);

            when(publicationRepository.findByTitleContainingIgnoreCase(searchTitle, pageable)).thenReturn(publicationPage);
            when(publicationMapper.toSummaryResponseDto(testPublication)).thenReturn(summaryResponseDto);

            // When
            Page<PublicationSummaryResponseDto> result = publicationService.searchPublicationsByTitle(searchTitle, pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Publication");

            verify(publicationRepository).findByTitleContainingIgnoreCase(searchTitle, pageable);
            verify(publicationMapper).toSummaryResponseDto(testPublication);
        }

        @Test
        @DisplayName("Should return empty page when no publications match search")
        void shouldReturnEmptyPageWhenNoPublicationsMatchSearch() {
            // Given
            String searchTitle = "NonExistent";
            Pageable pageable = PageRequest.of(0, 10);
            Page<Publication> emptyPage = new PageImpl<>(List.of(), pageable, 0);

            when(publicationRepository.findByTitleContainingIgnoreCase(searchTitle, pageable)).thenReturn(emptyPage);

            // When
            Page<PublicationSummaryResponseDto> result = publicationService.searchPublicationsByTitle(searchTitle, pageable);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();

            verify(publicationRepository).findByTitleContainingIgnoreCase(searchTitle, pageable);
            verifyNoInteractions(publicationMapper);
        }
    }

    @Nested
    @DisplayName("Delete Publication Tests")
    class DeletePublicationTests {

        @Test
        @DisplayName("Should delete publication successfully")
        void shouldDeletePublicationSuccessfully() {
            // Given
            when(publicationRepository.existsById(1L)).thenReturn(true);

            // When
            publicationService.deletePublication(1L);

            // Then
            verify(publicationRepository).existsById(1L);
            verify(publicationRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when publication not found")
        void shouldThrowResourceNotFoundExceptionWhenPublicationNotFound() {
            // Given
            when(publicationRepository.existsById(1L)).thenReturn(false);

            // When & Then
            assertThatThrownBy(() -> publicationService.deletePublication(1L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Publication not found with ID: 1");

            verify(publicationRepository).existsById(1L);
            verify(publicationRepository, never()).deleteById(1L);
        }
    }

    @Nested
    @DisplayName("Existence Check Tests")
    class ExistenceCheckTests {

        @Test
        @DisplayName("Should return true when publication exists by ID")
        void shouldReturnTrueWhenPublicationExistsById() {
            // Given
            when(publicationRepository.existsById(1L)).thenReturn(true);

            // When
            boolean result = publicationService.existsById(1L);

            // Then
            assertThat(result).isTrue();
            verify(publicationRepository).existsById(1L);
        }

        @Test
        @DisplayName("Should return false when publication does not exist by ID")
        void shouldReturnFalseWhenPublicationDoesNotExistById() {
            // Given
            when(publicationRepository.existsById(1L)).thenReturn(false);

            // When
            boolean result = publicationService.existsById(1L);

            // Then
            assertThat(result).isFalse();
            verify(publicationRepository).existsById(1L);
        }

        @Test
        @DisplayName("Should return true when publication exists by title")
        void shouldReturnTrueWhenPublicationExistsByTitle() {
            // Given
            when(publicationRepository.existsByTitle("Test Publication")).thenReturn(true);

            // When
            boolean result = publicationService.existsByTitle("Test Publication");

            // Then
            assertThat(result).isTrue();
            verify(publicationRepository).existsByTitle("Test Publication");
        }

        @Test
        @DisplayName("Should return false when publication does not exist by title")
        void shouldReturnFalseWhenPublicationDoesNotExistByTitle() {
            // Given
            when(publicationRepository.existsByTitle("NonExistent")).thenReturn(false);

            // When
            boolean result = publicationService.existsByTitle("NonExistent");

            // Then
            assertThat(result).isFalse();
            verify(publicationRepository).existsByTitle("NonExistent");
        }
    }
    @Nested
    @DisplayName("Grouped Publications Tests")
    class GroupedPublicationsTests {

        @Test
        @DisplayName("Should return grouped publications with books and magazines")
        void shouldReturnGroupedPublicationsWithBooksAndMagazines() {
            // Given
            Book book = new Book();
            book.setId(1L);
            book.setTitle("Book Title");

            Magazine magazine = new Magazine();
            magazine.setId(2L);
            magazine.setTitle("Magazine Title");

            BookSummaryResponseDto bookDto = new BookSummaryResponseDto(); // 👈 Correct type
            bookDto.setId(1L);
            bookDto.setTitle("Book Title");

            MagazineSummaryResponseDto magazineDto = new MagazineSummaryResponseDto(); // 👈 Correct type
            magazineDto.setId(2L);
            magazineDto.setTitle("Magazine Title");

            when(publicationRepository.findAllBooks()).thenReturn(List.of(book));
            when(publicationRepository.findAllMagazines()).thenReturn(List.of(magazine));
            when(publicationMapper.bookToSummaryDto(book)).thenReturn(bookDto);
            when(publicationMapper.magazineToSummaryDto(magazine)).thenReturn(magazineDto);

            // When
            GroupedPublicationsResponse result = publicationService.getAllPublicationsGroupedByType();

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getBooks()).hasSize(1);
            assertThat(result.getBooks().get(0).getId()).isEqualTo(1L);
            assertThat(result.getBooks().get(0).getTitle()).isEqualTo("Book Title");

            assertThat(result.getMagazines()).hasSize(1);
            assertThat(result.getMagazines().get(0).getId()).isEqualTo(2L);
            assertThat(result.getMagazines().get(0).getTitle()).isEqualTo("Magazine Title");

            verify(publicationRepository).findAllBooks();
            verify(publicationRepository).findAllMagazines();
            verify(publicationMapper).bookToSummaryDto(book);
            verify(publicationMapper).magazineToSummaryDto(magazine);
        }

        @Test
        @DisplayName("Should return empty lists when no books or magazines exist")
        void shouldReturnEmptyListsWhenNoBooksOrMagazinesExist() {
            // Given
            when(publicationRepository.findAllBooks()).thenReturn(List.of());
            when(publicationRepository.findAllMagazines()).thenReturn(List.of());

            // When
            GroupedPublicationsResponse result = publicationService.getAllPublicationsGroupedByType();

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getBooks()).isEmpty();
            assertThat(result.getMagazines()).isEmpty();

            verify(publicationRepository).findAllBooks();
            verify(publicationRepository).findAllMagazines();
            verifyNoInteractions(publicationMapper);
        }
    }
}