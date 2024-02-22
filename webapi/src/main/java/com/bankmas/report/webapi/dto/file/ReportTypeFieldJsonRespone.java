package com.bankmas.report.webapi.dto.file;

import com.bankmas.report.webapi.model.EnumReportTypeFieldJsonType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportTypeFieldJsonRespone {
    private String id;
    private String name;
    private EnumReportTypeFieldJsonType type;
}
