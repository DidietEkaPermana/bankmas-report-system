package com.bankmas.report.webapi.dto;

public class IdOnlyResponse {
    private String id;
    
    public IdOnlyResponse(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
