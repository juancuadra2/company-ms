package com.jcuadrado.company.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginatedResponseDto<T> {
    private List<T> data;
    private Integer totalPages;
    private Long totalElements;
    private Integer pageSize;
    private Integer currentPage;
}
