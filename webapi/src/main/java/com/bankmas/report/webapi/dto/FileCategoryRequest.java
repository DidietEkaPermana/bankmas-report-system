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
public class FileCategoryRequest implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	private String category;
}

