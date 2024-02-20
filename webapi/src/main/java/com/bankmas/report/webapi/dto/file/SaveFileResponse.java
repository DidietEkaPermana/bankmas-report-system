package com.bankmas.report.webapi.dto.file;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaveFileResponse {
    private String fileName;
    private String status;
    private String reason;
}
