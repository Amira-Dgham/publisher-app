package com.mobelite.publisher.ui.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Author {
    
    private Long id;
    private String name;
    private LocalDate birthDate;
    private String nationality;
    private List<Book> books;
    private List<Magazine> magazines;
} 