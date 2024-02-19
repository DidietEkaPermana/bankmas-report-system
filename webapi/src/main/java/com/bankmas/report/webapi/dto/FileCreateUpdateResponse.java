package com.bankmas.report.webapi.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileCreateUpdateResponse {
    private String message;
    private List<FileResponse> files;
}
