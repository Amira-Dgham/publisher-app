package com.mobelite.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mobelite.models.Magazine;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MagazineDataWrapper {
    private List<Magazine> content;
    // Optionally, add paging info if your API returns it
    // private int totalPages;
    // private long totalElements;
    // private boolean last;
}