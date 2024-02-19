package com.bankmas.report.serviceexcel.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bankmas.report.serviceexcel.model.MFile;

public interface FileRepository extends JpaRepository<MFile, String> {

}