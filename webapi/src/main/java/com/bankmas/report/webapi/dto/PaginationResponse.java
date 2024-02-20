package com.bankmas.report.webapi.dto;

import lombok.Data;

@Data
public class PaginationResponse<T> {
    private String message;
    private T data;
    private Integer page;
    private Integer totalPages;
    private Long totalElements;

    public PaginationResponse(String message, T data, Integer page, Integer totalPages, Long totalElements) {
        this.message = message;
        this.data = data;
        this.page = page;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    public PaginationResponse() {}
}
