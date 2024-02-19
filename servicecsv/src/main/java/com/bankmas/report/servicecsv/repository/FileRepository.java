package com.bankmas.report.servicecsv.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bankmas.report.servicecsv.model.MFile;

public interface FileRepository extends JpaRepository<MFile, String> {

}
