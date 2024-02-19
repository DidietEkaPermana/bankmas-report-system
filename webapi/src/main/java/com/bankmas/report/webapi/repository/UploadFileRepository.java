package com.bankmas.report.webapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bankmas.report.webapi.model.EnumDocumentFileType;
import com.bankmas.report.webapi.model.EnumUploadFileStatus;
import com.bankmas.report.webapi.model.UploadFile;

import java.util.Optional;


@Repository
public interface UploadFileRepository extends JpaRepository<UploadFile, String>{
    Optional<UploadFile> findFirstByDocumentFileTypeAndChecksumOrderByProcessDatetimeDesc(EnumDocumentFileType documentFileType, String checksum);
    Page<UploadFile> findAllByStatus(EnumUploadFileStatus status, Pageable pageable);
}
