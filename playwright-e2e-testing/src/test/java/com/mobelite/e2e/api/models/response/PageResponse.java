package com.mobelite.e2e.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {
    
    private List<T> content;
    private int number;
    private int size;
    private int totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;
    
    // Utility methods
    public boolean isEmpty() {
        return content == null || content.isEmpty();
    }
    
    public int getNumberOfElements() {
        return content != null ? content.size() : 0;
    }
    
    public boolean hasContent() {
        return !isEmpty();
    }
}