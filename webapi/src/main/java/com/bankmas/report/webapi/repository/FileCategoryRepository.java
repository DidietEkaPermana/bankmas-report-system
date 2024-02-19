package com.bankmas.report.webapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.bankmas.report.webapi.model.FileCategory;

public interface FileCategoryRepository extends CrudRepository<FileCategory, String> {

}
