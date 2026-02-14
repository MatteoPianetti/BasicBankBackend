package com.example.demo.dto.acount;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyTransactionPagination {
    private List<DailyTransactionResponse> content;
    private int pages;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
