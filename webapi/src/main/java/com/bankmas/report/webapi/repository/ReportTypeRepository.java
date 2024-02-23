package com.bankmas.report.webapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bankmas.report.webapi.model.ReportType;

@Repository("reportTypeRepository")
public interface ReportTypeRepository extends JpaRepository<ReportType, String> {
    Optional<ReportType> findFirstByName(String name);

    @Query(nativeQuery = true, value = "SELECT * FROM report_type WHERE name = :name AND id != :id")
    Optional<ReportType> findFirstByNameAndIdIsNot(String name, String id);
}
