package com.mobelite.publisherManagementSystem.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * Entity representing a magazine in the library system.
 * Extends Publication with magazine-specific properties.
 */
@Entity
@DiscriminatorValue("MAGAZINE")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class Magazine extends Publication {

    @Column(name = "issue_number", nullable = true)
    private Integer issueNumber;

    // Many-to-Many relationship with Author
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "magazine_authors",
            joinColumns = @JoinColumn(name = "magazine_id"),
            inverseJoinColumns = @JoinColumn(name = "author_id")
    )
    private List<Author> authors;
}
