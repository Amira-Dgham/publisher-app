package com.mobelite.publisherManagementSystem.repository;

import com.mobelite.publisherManagementSystem.entity.Magazine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Magazine entity operations.
 * Provides custom query methods for complex magazine searches.
 */
@Repository
public interface MagazineRepository extends JpaRepository<Magazine, Long> {


    /**
     * Find magazines by title containing given text (case-insensitive).
     * @param title The title to search for
     * @param pageable Pagination information
     * @return Page of magazines
     */
    Page<Magazine> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    /**
     * Find magazines by issue number.
     * @param issueNumber The issue number
     * @param pageable Pagination information
     * @return Page of magazines
     */
    Page<Magazine> findByIssueNumber(Integer issueNumber, Pageable pageable);


    /**
     * Find magazines by author ID.
     * @param authorId The author ID
     * @param pageable Pagination information
     * @return Page of magazines
     */
    @Query("SELECT m FROM Magazine m JOIN m.authors a WHERE a.id = :authorId")
    Page<Magazine> findByAuthorId(@Param("authorId") Long authorId, Pageable pageable);

    /**
     * Find magazines by multiple criteria.
     *
     * @param title       Title to search for (optional)
     * @param issueNumber Issue number (optional)
     * @param pageable    Pagination information
     * @return Page of magazines
     */
    @Query("SELECT m FROM Magazine m WHERE " +
            "(:title IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:issueNumber IS NULL OR m.issueNumber = :issueNumber) AND " +
            "(:startDate IS NULL OR m.publicationDate >= :startDate) AND " +
            "(:endDate IS NULL OR m.publicationDate <= :endDate)")
    default Page<Magazine> findByMultipleCriteria(
            @Param("title") String title,
            @Param("issueNumber") Integer issueNumber,
            Pageable pageable
    ) {
        return null;
    }

}