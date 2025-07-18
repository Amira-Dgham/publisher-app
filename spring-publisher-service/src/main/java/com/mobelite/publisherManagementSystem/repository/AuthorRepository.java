package com.mobelite.publisherManagementSystem.repository;

import com.mobelite.publisherManagementSystem.entity.Author;
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
    List<Author> findAllWithMagazines();

    boolean existsByName(String name);

    @EntityGraph(attributePaths = {"books", "magazines"})
    @Query("SELECT a FROM Author a WHERE a.id = :id")
    Optional<Author> findByIdWithPublications(Long id);
}