package com.bankmas.report.webapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bankmas.report.webapi.model.MFileCategoryField;

public interface FileCategoryFieldRepository extends JpaRepository<MFileCategoryField, String> {

}
