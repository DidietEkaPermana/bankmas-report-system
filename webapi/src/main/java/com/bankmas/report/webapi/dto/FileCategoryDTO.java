package com.bankmas.report.webapi.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileCategoryDTO implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String category;
	private String template;
}

