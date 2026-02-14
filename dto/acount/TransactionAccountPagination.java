package com.example.demo.dto.acount;

import java.util.List;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransactionAccountPagination {
    private List<TransactionAccountResponse> content;
    private int pages;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;
}
