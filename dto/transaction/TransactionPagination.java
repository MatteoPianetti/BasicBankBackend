package com.example.demo.dto.transaction;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionPagination {
    private List<TransactionResponse> content;
    private int pages;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
