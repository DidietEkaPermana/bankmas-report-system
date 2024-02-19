package com.bankmas.report.servicepdf.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bankmas.report.servicepdf.model.MFile;

public interface FileRepository extends JpaRepository<MFile, String> {

}
