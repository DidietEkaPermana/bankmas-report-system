package com.bankmas.report.webapi.dto.file;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.bankmas.report.webapi.model.EnumReportTypeFieldJsonType;
import com.bankmas.report.webapi.model.ReportType;
import com.bankmas.report.webapi.model.ReportTypeFieldJson;
import com.bankmas.report.webapi.util.DateUtil;
import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class DetailReportTypeResponse {
    private String id;
    private String name;
    private String createdDatetime;
    private String updatedDatetime;
    private List<JsonField> jsonFields = new ArrayList<>();

    public DetailReportTypeResponse(ReportType reportType, List<ReportTypeFieldJson> fieldJsons){
        this.id = reportType.getId();
        this.name = reportType.getName();
        this.createdDatetime = DateUtil.toString(reportType.getCreatedDatetime());
        this.updatedDatetime =DateUtil.toString(reportType.getUpdatedDatetime());

        for(ReportTypeFieldJson fieldJson : fieldJsons){
            jsonFields.add(new JsonField(fieldJson));
        }
    }

    public DetailReportTypeResponse(ReportType reportType){
        this.id = reportType.getId();
        this.name = reportType.getName();
        this.createdDatetime = DateUtil.toString(reportType.getCreatedDatetime());
        this.updatedDatetime =DateUtil.toString(reportType.getUpdatedDatetime());

        for(ReportTypeFieldJson fieldJson : reportType.getFieldJsons()){
            jsonFields.add(new JsonField(fieldJson));
        }
    }

    public DetailReportTypeResponse(){
        
    }

    @Data
    public static class JsonField{
        private String id;
        private String name;
        private EnumReportTypeFieldJsonType type;

        public JsonField(){}
        public JsonField(ReportTypeFieldJson field){
            this.id = field.getId();
            this.name = field.getName();
            this.type = field.getType();
        }
    }
}