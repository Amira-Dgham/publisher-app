package com.mobelite.publisherManagementSystem.entity;


import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * Entity representing a book in the library system.
 * Extends Publication with book-specific properties.
 */
@Entity
@DiscriminatorValue("BOOK")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class Book extends Publication {

    @Column(unique = true, nullable = true, length = 20)
    private String isbn;

    // Many-to-One relationship with Author
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = true)
    private Author author;
}
