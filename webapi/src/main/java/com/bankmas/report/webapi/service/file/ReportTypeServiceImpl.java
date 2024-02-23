package com.bankmas.report.webapi.service.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.bankmas.report.webapi.config.StorageProperties;
import com.bankmas.report.webapi.dto.DataResponse;
import com.bankmas.report.webapi.dto.IdOnlyResponse;
import com.bankmas.report.webapi.dto.file.UpsertReportTypeRequest;
import com.bankmas.report.webapi.dto.file.DetailReportTypeResponse;
import com.bankmas.report.webapi.exception.ValidationException;
import com.bankmas.report.webapi.model.ReportType;
import com.bankmas.report.webapi.model.ReportTypeFieldJson;
import com.bankmas.report.webapi.repository.ReportTypeFieldJsonRepository;
import com.bankmas.report.webapi.repository.ReportTypeRepository;
import com.bankmas.report.webapi.util.StringUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
@CacheConfig(cacheNames = "reportTypeCache")
public class ReportTypeServiceImpl implements ReportTypeService {

    @Autowired
    ReportTypeRepository reportTypeRepository;

    @Autowired
    ReportTypeFieldJsonRepository reportTypeFieldJsonRepository;

    @Autowired
    RedisTemplate<String, Serializable> redisTemplate;

    ObjectMapper objectMapper = new ObjectMapper();

    @Cacheable(cacheNames = "reportTypeFieldJsons", key = "#id", unless = "#result == null")
    public List<DetailReportTypeResponse.JsonField> getReportTypeFieldJsons(List<ReportTypeFieldJson> reportTypeFieldJsons) {
        return reportTypeFieldJsons.stream().map(DetailReportTypeResponse.JsonField::new).collect(Collectors.toList());
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
    public DataResponse<IdOnlyResponse> createReportType(UpsertReportTypeRequest request) throws JsonProcessingException {
        if(request.getJsonFields() == null || request.getJsonFields().isEmpty())
            throw new ValidationException("BAD_REQUEST");
        
        Optional<ReportType> optional = reportTypeRepository.findFirstByName(request.getName().toUpperCase(Locale.ROOT));
        if(optional.isPresent())
            throw new ValidationException("NAME_ALREADY_EXISTS");

        ReportType reportType = ReportType.builder()
            .name(request.getName().toUpperCase(Locale.ROOT))
            .build();

        String id = reportTypeRepository.save(reportType).getId();

        List<ReportTypeFieldJson> reportTypeFieldJsons = new ArrayList<>();
        for(UpsertReportTypeRequest.JsonField field : request.getJsonFields()) {
            ReportTypeFieldJson reportTypeFieldJson = ReportTypeFieldJson
                .builder()
                .reportType(reportType)
                .name(field.getName())
                .type(field.getType())
                .reportType(reportType)
                .build();

            reportTypeFieldJsons.add(reportTypeFieldJsonRepository.save(reportTypeFieldJson));
        }

        redisTemplate.opsForValue().set("reportTypeFieldJsons::" + id, objectMapper.writeValueAsString(this.getReportTypeFieldJsons(reportTypeFieldJsons))); 
        return new DataResponse<>("SUCCESS", new IdOnlyResponse(id));
    }

    @Override
    @Transactional(transactionManager = "transactionManager")
    @Caching(evict = { @CacheEvict(cacheNames = "reportTypes", key = "#id"),
		@CacheEvict(cacheNames = "reportTypes", allEntries = true) })
    public DataResponse<IdOnlyResponse> updateReportType(String id, UpsertReportTypeRequest request) throws JsonProcessingException {
        if(request.getJsonFields() == null || request.getJsonFields().isEmpty())
            throw new ValidationException("BAD_REQUEST");

        if(StringUtil.isNull(id))
            throw new ValidationException("BAD_REQUEST");

        ReportType reportType = reportTypeRepository.findById(id)
            .orElseThrow(() -> new ValidationException("DATA_NOT_EXISTS"));
        
        Optional<ReportType> optional = reportTypeRepository.findFirstByNameAndIdNot(request.getName().toUpperCase(Locale.ROOT), reportType.getId());
        if(optional.isPresent())
            throw new ValidationException("NAME_ALREADY_EXISTS");

        reportType.setName(request.getName().toUpperCase(Locale.ROOT));
        reportTypeRepository.save(reportType);

        reportTypeFieldJsonRepository.deleteByReportType(reportType);

        List<ReportTypeFieldJson> reportTypeFieldJsons = new ArrayList<>();
        for(UpsertReportTypeRequest.JsonField field : request.getJsonFields()) {
            ReportTypeFieldJson reportTypeFieldJson = ReportTypeFieldJson
                .builder()
                .reportType(reportType)
                .name(field.getName())
                .type(field.getType())
                .reportType(reportType)
                .build();

            reportTypeFieldJsons.add(reportTypeFieldJsonRepository.save(reportTypeFieldJson));
        }
        redisTemplate.opsForValue().set("reportTypeFieldJsons::" + id, objectMapper.writeValueAsString(this.getReportTypeFieldJsons(reportTypeFieldJsons)));
        return new DataResponse<>("SUCCESS", new IdOnlyResponse(reportType.getId()));
    }

	@Override
	public ResponseEntity<List<Map<String,String>>> generateReportTypeTemplate(String id) {
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


    @Override
    public ResponseEntity<?> downloadReportTypeTemplate(String id) {
        if(StringUtil.isNull(id))
            throw new ValidationException("BAD_REQUEST");
            
        Optional<ReportType> optional = reportTypeRepository.findById(id);
        if(optional.isPresent()) {
            ReportType reportType = optional.get();
            if(!StringUtil.isNull(reportType.getTemplateFile())) {

                byte[] fileBytes = null;
                
                try {
                    Path path = Paths.get(StorageProperties.getTemplateLocation() + reportType.getTemplateFile());
                    fileBytes = Files.readAllBytes(path);
                    return ResponseEntity.ok()
                        .header("Content-Disposition", "attachment; filename=" + reportType.getTemplateFile())
                        .header("Content-Type", "application/json")
                        .body(fileBytes);
                } catch (IOException e) {
                    if(e instanceof NoSuchFileException) {
                        throw new ValidationException("NOT_FOUND");
                    } else {
                        throw new ValidationException("INTERNAL_SERVER_ERROR");
                    }
                }
                
            } else {
                return generateReportTypeTemplate(id);
            }
        } else {
            throw new ValidationException("NOT_FOUND");
        }
    }


    @Override
    @Transactional(transactionManager = "transactionManager")
    public DataResponse<IdOnlyResponse> uploadReportTypeTemplate(String id, MultipartFile multipartFile) throws IOException {
        ReportType reportType = reportTypeRepository.findById(id).orElseThrow(() -> new ValidationException("DATA_NOT_EXISTS"));
        
        InputStream inputStream = multipartFile.getInputStream();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while((line=reader.readLine()) != null)
            stringBuilder.append(line);

        inputStream.close();
        reader.close();

        List<Map<String, Object>> listMap = new ArrayList<>();
        try{
            listMap = objectMapper.readValue(stringBuilder.toString(), List.class);
        } catch(IOException e){
            throw new ValidationException("INVALID_FILE_FORMAT");
        }

        String oldFilename = reportType.getTemplateFile();

        String fileName = StringUtil.generateFilename() + ".json";
        reportType.setTemplateFile(fileName);

        String path = StorageProperties.getTemplateLocation() + fileName ;
        File file = new File(path);
        FileWriter fileWriter = new FileWriter(file);
        fileWriter.write(stringBuilder.toString());
        fileWriter.close();

        if(!StringUtil.isNull(oldFilename)) {
            Files.delete(Paths.get(StorageProperties.getTemplateLocation() + oldFilename));
        }

        reportTypeRepository.save(reportType);

        return new DataResponse("SUCCESS", new IdOnlyResponse(reportType.getId()));
    }

    
}
