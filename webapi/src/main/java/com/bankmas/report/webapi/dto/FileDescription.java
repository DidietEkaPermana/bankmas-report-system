package com.bankmas.report.webapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class FileDescription {
	private String format;
	private String type;
}
