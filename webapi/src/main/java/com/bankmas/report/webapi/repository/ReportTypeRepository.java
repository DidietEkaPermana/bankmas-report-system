package com.bankmas.report.webapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bankmas.report.webapi.model.ReportType;

@Repository
public interface ReportTypeRepository extends JpaRepository<ReportType, String> {
    Optional<ReportType> findFirstByName(String name);

    Optional<ReportType> findFirstByNameAndIdNot(@Param(value = "name") String name, @Param(value = "id") String id);
}
