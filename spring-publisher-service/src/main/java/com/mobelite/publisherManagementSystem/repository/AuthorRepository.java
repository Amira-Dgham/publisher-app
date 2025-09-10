package com.mobelite.publisherManagementSystem.repository;

import com.mobelite.publisherManagementSystem.entity.Author;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {


    @EntityGraph(attributePaths = {"magazines"})
    @Query("SELECT a FROM Author a")
    Page<Author> findAllWithMagazines(Pageable pageable);

    boolean existsByName(String name);

    @EntityGraph(attributePaths = {"books", "magazines"})
    @Query("SELECT a FROM Author a WHERE a.id = :id")
    Optional<Author> findByIdWithPublications(Long id);
}