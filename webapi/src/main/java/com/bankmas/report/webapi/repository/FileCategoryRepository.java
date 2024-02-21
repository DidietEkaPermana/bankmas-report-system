package com.bankmas.report.webapi.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bankmas.report.webapi.model.MFileCategory;

public interface FileCategoryRepository extends JpaRepository<MFileCategory, String> {

	@Query(value = "SELECT p "
			+ " FROM MFileCategory p "
			+ " WHERE UPPER(p.category) LIKE %:category% ")
	public List<MFileCategory> findByCategory(@Param("category") String category);
}
