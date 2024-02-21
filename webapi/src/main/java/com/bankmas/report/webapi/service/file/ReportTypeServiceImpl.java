package com.bankmas.report.webapi.service.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.catalina.connector.Request;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bankmas.report.webapi.dto.DataResponse;
import com.bankmas.report.webapi.dto.IdOnlyResponse;
import com.bankmas.report.webapi.dto.PaginationResponse;
import com.bankmas.report.webapi.dto.file.UpsertReportTypeRequest;
import com.bankmas.report.webapi.dto.file.DetailReportTypeResponse;
import com.bankmas.report.webapi.dto.file.ListReportType;
import com.bankmas.report.webapi.exception.ValidationException;
import com.bankmas.report.webapi.model.ReportType;
import com.bankmas.report.webapi.model.ReportTypeFieldJson;
import com.bankmas.report.webapi.repository.ReportTypeFieldJsonRepository;
import com.bankmas.report.webapi.repository.ReportTypeRepository;
import com.bankmas.report.webapi.util.StringUtil;

@Service
@CacheConfig(cacheNames = "reportTypeCache")
public class ReportTypeServiceImpl implements ReportTypeService {

    @Autowired
    ReportTypeRepository reportTypeRepository;

    @Autowired
    ReportTypeFieldJsonRepository reportTypeFieldJsonRepository;

    @Override
    @Cacheable(cacheNames = "reportTypeFieldJsons", key = "#id", unless = "#result == null")
    public List<DetailReportTypeResponse.JsonField> getReportTypeFieldJsons(String id) {
        if(StringUtil.isNull(id))
            throw new ValidationException("BAD_REQUEST");
        
        ReportType reportType = reportTypeRepository.findById(id).orElseThrow(() -> new ValidationException("REPORT_TYPE_NOT_EXISTS"));
        return new DetailReportTypeResponse(reportType).getJsonFields();
    }


    @Override
    @Cacheable(cacheNames = "reportTypes")
    public DataResponse<List<DetailReportTypeResponse>> listReportType() {
        List<ReportType> reportTypes = reportTypeRepository.findAll(Sort.by(Direction.ASC, "name"));
        return new DataResponse<List<DetailReportTypeResponse>>("SUCCESS", reportTypes.stream().map(DetailReportTypeResponse::new).collect(Collectors.toList()));
    }

    @Override
    @Cacheable(cacheNames = "reportTypes", key = "#id", unless = "#result == null")
    public DataResponse<DetailReportTypeResponse> getReportType(String id) {
        if(StringUtil.isNull(id))
            throw new ValidationException("BAD_REQUEST");

        ReportType reportType = reportTypeRepository.findById(id)
            .orElseThrow(() -> new ValidationException("DATA_NOT_EXISTS"));

        List<ReportTypeFieldJson> fieldJsons = reportTypeFieldJsonRepository.findAllByReportTypeOrderByCreatedDatetimeAsc(reportType);

        return new DataResponse<DetailReportTypeResponse>("SUCCESS", new DetailReportTypeResponse(reportType, fieldJsons));
        
    }

    @Override
    @Transactional(transactionManager = "transactionManager")
    @CacheEvict(cacheNames = "reportTypes", allEntries = true)
    public DataResponse<IdOnlyResponse> createReportType(UpsertReportTypeRequest request) {
        if(request.getJsonFields() == null || request.getJsonFields().isEmpty())
            throw new ValidationException("BAD_REQUEST");
        
        Optional<ReportType> optional = reportTypeRepository.findFirstByName(request.getName().toUpperCase(Locale.ROOT));
        if(optional.isPresent())
            throw new ValidationException("NAME_ALREADY_EXISTS");

        ReportType reportType = ReportType.builder()
            .name(request.getName().toUpperCase(Locale.ROOT))
            .build();

        String id = reportTypeRepository.save(reportType).getId();

        for(UpsertReportTypeRequest.JsonField field : request.getJsonFields()) {
            ReportTypeFieldJson reportTypeFieldJson = ReportTypeFieldJson
                .builder()
                .reportType(reportType)
                .name(field.getName())
                .type(field.getType())
                .reportType(reportType)
                .build();

            reportTypeFieldJsonRepository.save(reportTypeFieldJson);
        }

        return new DataResponse<>("SUCCESS", new IdOnlyResponse(id));
    }

    @Override
    @Transactional(transactionManager = "transactionManager")
    @Caching(evict = { @CacheEvict(cacheNames = "reportTypes", key = "#id"),
			@CacheEvict(cacheNames = "reportTypes", allEntries = true) })
    public DataResponse<IdOnlyResponse> updateReportType(String id, UpsertReportTypeRequest request) {
        if(request.getJsonFields() == null || request.getJsonFields().isEmpty())
            throw new ValidationException("BAD_REQUEST");

        if(StringUtil.isNull(id))
            throw new ValidationException("BAD_REQUEST");

        ReportType reportType = reportTypeRepository.findById(id)
            .orElseThrow(() -> new ValidationException("DATA_NOT_EXISTS"));
        
        Optional<ReportType> optional = reportTypeRepository.findFirstByNameAndIdIsNot(request.getName().toUpperCase(Locale.ROOT), id);
        if(optional.isPresent())
            new ValidationException("NAME_ALREADY_EXISTS");

        reportType.setName(request.getName().toUpperCase(Locale.ROOT));
        reportTypeRepository.save(reportType);

        reportTypeFieldJsonRepository.deleteByReportType(reportType);
        for(UpsertReportTypeRequest.JsonField field : request.getJsonFields()) {
            ReportTypeFieldJson reportTypeFieldJson = ReportTypeFieldJson
                .builder()
                .reportType(reportType)
                .name(field.getName())
                .type(field.getType())
                .reportType(reportType)
                .build();

            reportTypeFieldJsonRepository.save(reportTypeFieldJson);
        }

        return new DataResponse<>("SUCCESS", new IdOnlyResponse(reportType.getId()));
    }

	@Override
	public ResponseEntity<List<Map<String,String>>> downloadReportTypeTemplate(String id) {
		if(StringUtil.isNull(id))
            throw new ValidationException("BAD_REQUEST");

        ReportType reportType = reportTypeRepository.findById(id)
            .orElseThrow(() -> new ValidationException("DATA_NOT_EXISTS"));

        List<ReportTypeFieldJson> fieldJsons = reportTypeFieldJsonRepository.findAllByReportTypeOrderByCreatedDatetimeAsc(reportType);
        
        Map<String, String> map = new HashMap<>();
        for(ReportTypeFieldJson fieldJson : fieldJsons){
            map.put(fieldJson.getName(), "STRING");
        }
        
        List<Map<String,String>> result = List.of(map);

        return ResponseEntity.ok()
            .header("Content-Disposition", "attachment; filename=" + reportType.getName()+".json")
            .header("Content-Type", "application/json")
            .body(result);
	}
}
