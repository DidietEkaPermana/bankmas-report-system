package com.bankmas.report.webapi.dto;

import java.util.List;

import com.bankmas.report.webapi.model.MFileCategoryField;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MFileCategoryDTO {
	private String id;
	private String category;
	private List<MFileCategoryField> fileCategoryField;
}
