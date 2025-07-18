package com.mobelite.publisherManagementSystem.repository;
import com.mobelite.publisherManagementSystem.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Book entity operations.
 * Provides custom query methods for specific business requirements.
 */
@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    /**
     * Find a book by its ISBN.
     * @param isbn The ISBN to search for
     * @return Optional containing the book if found
     */
    Optional<Book> findByIsbn(String isbn);

    /**
     * Check if a book exists with the given ISBN.
     * @param isbn The ISBN to check
     * @return true if book exists, false otherwise
     */
    boolean existsByIsbn(String isbn);

    /**
     * Check if a book exists with the given ISBN, excluding a specific ID.
     * Useful for update operations to avoid self-conflict.
     * @param isbn The ISBN to check
     * @param id The ID to exclude from the check
     * @return true if book exists, false otherwise
     */
    boolean existsByIsbnAndIdNot(String isbn, Long id);

    /**
     * Find books by author ID with pagination.
     * @param authorId The author ID
     * @param pageable Pagination information
     * @return Page of books
     */
    Page<Book> findByAuthorId(Long authorId, Pageable pageable);

    /**
     * Find books by title containing the given text (case-insensitive).
     * @param title The title text to search for
     * @param pageable Pagination information
     * @return Page of books
     */
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    /**
     * Find books published between two dates.
     * @param startDate The start date
     * @param endDate The end date
     * @param pageable Pagination information
     * @return Page of books
     */
    Page<Book> findByPublicationDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);


    /**
     * Custom query to find books with their authors using JOIN FETCH to avoid N+1 problem.
     * @param pageable Pagination information
     * @return List of books with authors
     */
    @Query("SELECT b FROM Book b JOIN FETCH b.author")
    List<Book> findAllWithAuthors(Pageable pageable);

    /**
     * Custom query to search books by multiple criteria.
     *
     * @param title      Title search term
     * @param authorName Author name search term
     * @param pageable   Pagination information
     * @return Page of books
     */
    @Query("SELECT b FROM Book b JOIN b.author a WHERE " +
            "(:title IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:authorName IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :authorName, '%'))) ")
    default Page<Book> findByMultipleCriteria(@Param("title") String title,
                                              @Param("authorName") String authorName,
                                              Pageable pageable) {
        return null;
    }
}