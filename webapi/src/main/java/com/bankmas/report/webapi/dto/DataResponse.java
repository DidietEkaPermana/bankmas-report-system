package com.bankmas.report.webapi.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DataResponse<T> {
    private String message;
    private T data;

    public DataResponse(String message, T data) {   
        this.message = message;
        this.data = data;
    }

    public DataResponse() {}
}
