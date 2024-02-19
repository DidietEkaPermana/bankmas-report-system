package com.bankmas.report.webapi.repository;

import com.bankmas.report.webapi.model.MFileJenisReport;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FileJenisReportRepository extends JpaRepository<MFileJenisReport, String> {
    List<MFileJenisReport> findByNamaReport(String namaReport);
}
