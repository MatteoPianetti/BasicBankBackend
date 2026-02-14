package com.example.demo.dto.card;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CardPagination {
    List<CardResponse> content;
    private int pages;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
