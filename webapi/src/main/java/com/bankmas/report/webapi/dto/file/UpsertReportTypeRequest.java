package com.bankmas.report.webapi.dto.file;

import java.util.List;
import com.bankmas.report.webapi.model.EnumReportTypeFieldJsonType;

import lombok.Data;

@Data
public class UpsertReportTypeRequest {
    private String name;
    private List<JsonField> jsonFields;

    public UpsertReportTypeRequest(){}

    public UpsertReportTypeRequest(String name, List<JsonField> jsonFields){
        this.name = name;
        this.jsonFields = jsonFields;
    }

    @Data
    public static class JsonField{
        private String name;
        private EnumReportTypeFieldJsonType type;

        public JsonField(){}
        public JsonField(String name, EnumReportTypeFieldJsonType type){
            this.name = name;
            this.type = type;
        }
    }
}
