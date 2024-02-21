package com.bankmas.report.webapi.service;

import org.springframework.stereotype.Service;

import com.bankmas.report.webapi.dto.MFileCategoryDTO;
import com.bankmas.report.webapi.model.MFileCategory;

import java.util.function.Function;

@Service
public class MFileCategoryDTOMapper implements Function<MFileCategory, MFileCategoryDTO> {

	@Override
	public MFileCategoryDTO apply(MFileCategory t) {
		return new MFileCategoryDTO(
				t.getId(), t.getCategory(), t.getMFileCategoryFields());
	}

}
