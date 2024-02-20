package com.bankmas.report.webapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bankmas.report.webapi.model.ReportTypeFieldJson;
import java.util.List;
import com.bankmas.report.webapi.model.ReportType;


@Repository("reportTypeFieldJsonRepository")
public interface ReportTypeFieldJsonRepository extends JpaRepository<ReportTypeFieldJson, String> {
    List<ReportTypeFieldJson> findAllByReportTypeOrderByCreatedDatetimeAsc(ReportType reportType);
    void deleteByReportType(ReportType reportType);
}
