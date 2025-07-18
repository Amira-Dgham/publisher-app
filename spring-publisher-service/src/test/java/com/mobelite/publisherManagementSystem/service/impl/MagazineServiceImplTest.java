package com.mobelite.publisherManagementSystem.service.impl;

import com.mobelite.publisherManagementSystem.dto.request.magazine.MagazineRequestDto;
import com.mobelite.publisherManagementSystem.dto.response.magazine.MagazineResponseDto;
import com.mobelite.publisherManagementSystem.dto.response.magazine.MagazineSummaryResponseDto;
import com.mobelite.publisherManagementSystem.entity.Author;
import com.mobelite.publisherManagementSystem.entity.Magazine;
import com.mobelite.publisherManagementSystem.exception.ResourceNotFoundException;
import com.mobelite.publisherManagementSystem.mapper.MagazineMapper;
import com.mobelite.publisherManagementSystem.repository.AuthorRepository;
import com.mobelite.publisherManagementSystem.repository.MagazineRepository;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MagazineServiceImpl Unit Tests")
class MagazineServiceImplTest {

    @Mock
    private MagazineMapper magazineMapper;

    @Mock
    private MagazineRepository magazineRepository;

    @Mock
    private AuthorRepository authorRepository;

    @InjectMocks
    private MagazineServiceImpl magazineService;

    // Test data
    private MagazineRequestDto requestDto;
    private Magazine magazine;
    private MagazineResponseDto responseDto;
    private MagazineSummaryResponseDto summaryDto;
    private Author author1;
    private Author author2;
    private List<Author> authors;
    private List<Long> authorIds;

    @BeforeEach
    void setUp() {
        // Create test authors
        author1 = new Author();
        author1.setId(1L);
        author1.setName("John Doe");

        author2 = new Author();
        author2.setId(2L);
        author2.setName("Jane Smith");

        authors = Arrays.asList(author1, author2);
        authorIds = Arrays.asList(1L, 2L);

        // Create test request DTO
        requestDto = new MagazineRequestDto();
        requestDto.setTitle("Test Magazine");
        requestDto.setAuthorIds(authorIds);

        // Create test magazine entity
        magazine = new Magazine();
        magazine.setId(1L);
        magazine.setTitle("Test Magazine");
        magazine.setAuthors(authors);

        // Create test response DTO
        responseDto = new MagazineResponseDto();
        responseDto.setTitle("Test Magazine");

        // Create test summary DTO
        summaryDto = new MagazineSummaryResponseDto();
        summaryDto.setTitle("Test Magazine");
    }

    @Nested
    @DisplayName("Create Magazine Tests")
    class CreateMagazineTests {

        @Test
        @DisplayName("Should create magazine successfully when all authors exist")
        void shouldCreateMagazineSuccessfully_WhenAllAuthorsExist() {
            // Arrange
            when(authorRepository.findAllById(authorIds)).thenReturn(authors);
            when(magazineMapper.toEntity(requestDto)).thenReturn(magazine);
            when(magazineRepository.save(magazine)).thenReturn(magazine);
            when(magazineMapper.toResponseDto(magazine)).thenReturn(responseDto);

            // Act
            MagazineResponseDto result = magazineService.createMagazine(requestDto);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("Test Magazine");

            verify(authorRepository).findAllById(authorIds);
            verify(magazineMapper).toEntity(requestDto);
            verify(magazineRepository).save(magazine);
            verify(magazineMapper).toResponseDto(magazine);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when some authors not found")
        void shouldThrowResourceNotFoundException_WhenSomeAuthorsNotFound() {
            // Arrange
            List<Author> partialAuthors = Collections.singletonList(author1);
            when(authorRepository.findAllById(authorIds)).thenReturn(partialAuthors);

            // Act & Assert
            assertThatThrownBy(() -> magazineService.createMagazine(requestDto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("One or more authors not found");

            verify(authorRepository).findAllById(authorIds);
            verify(magazineMapper, never()).toEntity(any());
            verify(magazineRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when no authors found")
        void shouldThrowResourceNotFoundException_WhenNoAuthorsFound() {
            // Arrange
            when(authorRepository.findAllById(authorIds)).thenReturn(Collections.emptyList());

            // Act & Assert
            assertThatThrownBy(() -> magazineService.createMagazine(requestDto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("One or more authors not found");

            verify(authorRepository).findAllById(authorIds);
            verify(magazineMapper, never()).toEntity(any());
            verify(magazineRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Update Magazine Tests")
    class UpdateMagazineTests {

        @Test
        @DisplayName("Should update magazine successfully when magazine and authors exist")
        void shouldUpdateMagazineSuccessfully_WhenMagazineAndAuthorsExist() {
            // Arrange
            Long magazineId = 1L;
            when(magazineRepository.findById(magazineId)).thenReturn(Optional.of(magazine));
            when(authorRepository.findAllById(authorIds)).thenReturn(authors);
            when(magazineRepository.save(magazine)).thenReturn(magazine);
            when(magazineMapper.toResponseDto(magazine)).thenReturn(responseDto);

            // Act
            MagazineResponseDto result = magazineService.updateMagazine(magazineId, requestDto);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("Test Magazine");

            verify(magazineRepository).findById(magazineId);
            verify(authorRepository).findAllById(authorIds);
            verify(magazineMapper).updateEntityFromDto(requestDto, magazine);
            verify(magazineRepository).save(magazine);
            verify(magazineMapper).toResponseDto(magazine);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when magazine not found")
        void shouldThrowResourceNotFoundException_WhenMagazineNotFound() {
            // Arrange
            Long magazineId = 999L;
            when(magazineRepository.findById(magazineId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> magazineService.updateMagazine(magazineId, requestDto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Magazine not found with ID: " + magazineId);

            verify(magazineRepository).findById(magazineId);
            verify(authorRepository, never()).findAllById(any());
            verify(magazineRepository, never()).save(any());
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when some authors not found during update")
        void shouldThrowResourceNotFoundException_WhenSomeAuthorsNotFoundDuringUpdate() {
            // Arrange
            Long magazineId = 1L;
            List<Author> partialAuthors = Collections.singletonList(author1);
            when(magazineRepository.findById(magazineId)).thenReturn(Optional.of(magazine));
            when(authorRepository.findAllById(authorIds)).thenReturn(partialAuthors);

            // Act & Assert
            assertThatThrownBy(() -> magazineService.updateMagazine(magazineId, requestDto))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("One or more authors not found");

            verify(magazineRepository).findById(magazineId);
            verify(authorRepository).findAllById(authorIds);
            verify(magazineRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get Magazine By Id Tests")
    class GetMagazineByIdTests {

        @Test
        @DisplayName("Should return magazine when found by id")
        void shouldReturnMagazine_WhenFoundById() {
            // Arrange
            Long magazineId = 1L;
            when(magazineRepository.findById(magazineId)).thenReturn(Optional.of(magazine));
            when(magazineMapper.toResponseDto(magazine)).thenReturn(responseDto);

            // Act
            MagazineResponseDto result = magazineService.getMagazineById(magazineId);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo("Test Magazine");

            verify(magazineRepository).findById(magazineId);
            verify(magazineMapper).toResponseDto(magazine);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when magazine not found by id")
        void shouldThrowResourceNotFoundException_WhenMagazineNotFoundById() {
            // Arrange
            Long magazineId = 999L;
            when(magazineRepository.findById(magazineId)).thenReturn(Optional.empty());

            // Act & Assert
            assertThatThrownBy(() -> magazineService.getMagazineById(magazineId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Magazine not found with ID: " + magazineId);

            verify(magazineRepository).findById(magazineId);
            verify(magazineMapper, never()).toResponseDto(any());
        }
    }

    @Nested
    @DisplayName("Get All Magazines Tests")
    class GetAllMagazinesTests {

        @Test
        @DisplayName("Should return paginated magazines successfully")
        void shouldReturnPaginatedMagazines_Successfully() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            List<Magazine> magazineList = Arrays.asList(magazine);
            Page<Magazine> magazinePage = new PageImpl<>(magazineList, pageable, 1);

            when(magazineRepository.findAll(pageable)).thenReturn(magazinePage);
            when(magazineMapper.toSummaryDto(magazine)).thenReturn(summaryDto);

            // Act
            Page<MagazineSummaryResponseDto> result = magazineService.getAllMagazines(pageable);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getTitle()).isEqualTo("Test Magazine");
            assertThat(result.getTotalElements()).isEqualTo(1);

            verify(magazineRepository).findAll(pageable);
            verify(magazineMapper).toSummaryDto(magazine);
        }

        @Test
        @DisplayName("Should return empty page when no magazines found")
        void shouldReturnEmptyPage_WhenNoMagazinesFound() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Magazine> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

            when(magazineRepository.findAll(pageable)).thenReturn(emptyPage);

            // Act
            Page<MagazineSummaryResponseDto> result = magazineService.getAllMagazines(pageable);

            // Assert
            assertThat(result).isNotNull();
            assertThat(result.getContent()).isEmpty();
            assertThat(result.getTotalElements()).isEqualTo(0);

            verify(magazineRepository).findAll(pageable);
            verify(magazineMapper, never()).toSummaryDto(any());
        }
    }

    @Nested
    @DisplayName("Delete Magazine Tests")
    class DeleteMagazineTests {

        @Test
        @DisplayName("Should delete magazine successfully when exists")
        void shouldDeleteMagazineSuccessfully_WhenExists() {
            // Arrange
            Long magazineId = 1L;
            when(magazineRepository.existsById(magazineId)).thenReturn(true);

            // Act
            magazineService.deleteMagazine(magazineId);

            // Assert
            verify(magazineRepository).existsById(magazineId);
            verify(magazineRepository).deleteById(magazineId);
        }

        @Test
        @DisplayName("Should throw ResourceNotFoundException when magazine not found for deletion")
        void shouldThrowResourceNotFoundException_WhenMagazineNotFoundForDeletion() {
            // Arrange
            Long magazineId = 999L;
            when(magazineRepository.existsById(magazineId)).thenReturn(false);

            // Act & Assert
            assertThatThrownBy(() -> magazineService.deleteMagazine(magazineId))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessage("Magazine not found with ID: " + magazineId);

            verify(magazineRepository).existsById(magazineId);
            verify(magazineRepository, never()).deleteById(any());
        }
    }

    @Nested
    @DisplayName("Edge Cases and Boundary Tests")
    class EdgeCasesTests {


        @Test
        @DisplayName("Should handle large author list correctly")
        void shouldHandleLargeAuthorList_Correctly() {
            // Arrange
            List<Long> largeAuthorIds = Arrays.asList(1L, 2L, 3L, 4L, 5L);
            List<Author> largeAuthorList = Arrays.asList(
                    author1, author2, new Author(), new Author(), new Author()
            );

            requestDto.setAuthorIds(largeAuthorIds);
            when(authorRepository.findAllById(largeAuthorIds)).thenReturn(largeAuthorList);
            when(magazineMapper.toEntity(requestDto)).thenReturn(magazine);
            when(magazineRepository.save(magazine)).thenReturn(magazine);
            when(magazineMapper.toResponseDto(magazine)).thenReturn(responseDto);

            // Act
            MagazineResponseDto result = magazineService.createMagazine(requestDto);

            // Assert
            assertThat(result).isNotNull();
            verify(authorRepository).findAllById(largeAuthorIds);
        }
    }
}