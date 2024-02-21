package com.bankmas.report.webapi.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bankmas.report.webapi.dto.FileResponse;
import com.bankmas.report.webapi.model.MFile;

public interface FileRepository extends JpaRepository<MFile, String> {

	@Query(value = "SELECT NEW com.bankmas.report.webapi.dto.FileResponse (p.id, p.fileName, p.status) "
			+ " FROM MFile p "
			+ " WHERE (p.status = :status OR :status IS NULL) ")
	public List<FileResponse> findByCriteria(@Param("status") String status, Pageable pageRequest);
	
	@Query(value = "SELECT NEW com.bankmas.report.webapi.dto.FileResponse (p.id, p.fileName, p.status) "
			+ " FROM MFile p "
			+ " WHERE (p.status = :status OR :status IS NULL) ")
	public List<FileResponse> findByCriteria(@Param("status") String status);
	
	@Query(value = "SELECT p "
			+ " FROM MFile p "
			+ " WHERE (p.status = :status OR :status IS NULL) AND p.id = :id ")
	public List<MFile> findByIdAndStatus(@Param("status") String status, @Param("id") String id);
	
	@Query(value = "SELECT p "
			+ " FROM MFile p "
			+ " WHERE (p.checksumFile2 = :checksumFile2) ")
	public List<MFile> findByChecksumFile2(@Param("checksumFile2") String checksumFile2);
	
	@Query(value = "SELECT NEW com.bankmas.report.webapi.dto.FileResponse (p.id, p.fileName, p.status, p.createdAt, p.updatedAt) "
			+ " FROM MFile p "
			+ " WHERE (p.status = :status OR :status IS NULL) ")
	public List<FileResponse> findByCriteria2(@Param("status") String status, Pageable pageRequest);
}
