package com.bankmas.report.webapi.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PagingRequest {
	private int currentPage;
	private int pageItems;
	private Map<String, Object> filters;
	private String orderBy;
	private boolean direction;
}
