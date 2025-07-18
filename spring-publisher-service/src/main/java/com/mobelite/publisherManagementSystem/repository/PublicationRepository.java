package com.mobelite.publisherManagementSystem.repository;

import com.mobelite.publisherManagementSystem.entity.Book;
import com.mobelite.publisherManagementSystem.entity.Magazine;
import com.mobelite.publisherManagementSystem.entity.Publication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository interface for Publication entity.
 * Provides CRUD operations and custom query methods.
 */
@Repository
public interface PublicationRepository extends JpaRepository<Publication, Long> {

    /**
     * Find publications by title containing the given text (case-insensitive).
     * @param title The title to search for
     * @param pageable Pagination information
     * @return Page of publications
     */
    @Query("SELECT p FROM Publication p WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :title, '%'))")
    Page<Publication> findByTitleContainingIgnoreCase(@Param("title") String title, Pageable pageable);

    @Query("SELECT b FROM Book b")
    List<Book> findAllBooks();

    @Query("SELECT m FROM Magazine m")
    List<Magazine> findAllMagazines();

    /**
     * Check if a publication exists by title.
     * @param title The title to check
     * @return true if exists, false otherwise
     */
    boolean existsByTitle(String title);

}