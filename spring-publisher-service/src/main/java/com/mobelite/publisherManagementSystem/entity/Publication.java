package com.mobelite.publisherManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

/**
 * Abstract base entity for all publications in the library system.
 * Uses SINGLE_TABLE inheritance strategy for better performance.
 */
@Entity
@Table(name = "publications", uniqueConstraints = {
        @UniqueConstraint(columnNames = "title")
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "publication_type", discriminatorType = DiscriminatorType.STRING)
@Data
@NoArgsConstructor
@SuperBuilder
public abstract class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(name = "publication_date", nullable = false)
    private LocalDate publicationDate;
}