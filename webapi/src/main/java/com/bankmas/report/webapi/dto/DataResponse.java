package com.bankmas.report.webapi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DataResponse {
    private String message;
    private Object data;
}
