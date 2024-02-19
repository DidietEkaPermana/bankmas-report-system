package com.bankmas.report.webapi.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PagingResponse {
	private List<Object> data;
	private Long numberOfPages;
}
