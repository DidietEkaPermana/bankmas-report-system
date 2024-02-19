package com.bankmas.report.webapi.repository;

import com.bankmas.report.webapi.model.MFileUpload;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FileUploadRepository extends JpaRepository<MFileUpload, String> {

    Page<MFileUpload> findBystatusProses(String status_proses, Pageable pageable);
    Page<MFileUpload> findAll(Pageable pageable);
    
    List<MFileUpload> findBystatusProses(String status_proses);

    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM MFileUpload e WHERE e.checksumFile = :checksum AND e.statusProses = 'finish'")
    boolean existsByChecksumAndStatus(@Param("checksum") String checksum);

}
