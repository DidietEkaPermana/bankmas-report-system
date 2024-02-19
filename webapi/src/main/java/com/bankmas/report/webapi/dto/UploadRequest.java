package com.bankmas.report.webapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UploadRequest {
	private String wilayah;
	private String tanggal;
	private String gambar;
}
