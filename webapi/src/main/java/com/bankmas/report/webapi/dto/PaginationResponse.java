package com.bankmas.report.webapi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaginationResponse {
    private String message;
    private Object data;
    private Integer page;
    private Integer totalPages;
    private Long totalElements;
}
