package com.bankmas.report.webapi.dto.file;

import com.bankmas.report.webapi.model.ReportType;
import com.bankmas.report.webapi.util.DateUtil;

import lombok.Data;

@Data
public class ListReportType {
    private String id;
    private String name;
    private String createdDatetime;
    private String updatedDatetime;

    public ListReportType(){}
    public ListReportType(ReportType reportType){
        this.id = reportType.getId();
        this.name = reportType.getName();
        this.createdDatetime = DateUtil.toString(reportType.getCreatedDatetime());
        this.updatedDatetime = DateUtil.toString(reportType.getUpdatedDatetime());
    }
}
