package com.bankmas.report.webapi.dto;

import java.sql.Timestamp;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileResponse {
	private String id;
	private String fileName;
	private String status;
	private Date createdAt;
	private Date updatedAt;
	
	public FileResponse(String id, String fileName, String status) {
		this.id = id;
		this.fileName = fileName;
		this.status = status;
	}
	
	public FileResponse(String id, String fileName, String status, Timestamp createdAt, Timestamp updatedAt) {
		this.id = id;
		this.fileName = fileName;
		this.status = status;
		this.createdAt = convertTimestamp(createdAt);
		this.updatedAt = convertTimestamp(updatedAt);
	}
	
	//TODO
	public static Date convertTimestamp(Timestamp ts) {
        Date date = new Date(ts.getTime());

        return date;
    }
}
