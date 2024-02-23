package com.bankmas.report.webapi.service.file;

import com.bankmas.report.webapi.dto.file.DetailReportTypeResponse;
import com.bankmas.report.webapi.model.EnumReportTypeFieldJsonType;
import com.bankmas.report.webapi.model.ReportType;
import com.bankmas.report.webapi.model.ReportTypeFieldJson;
import com.bankmas.report.webapi.repository.ReportTypeFieldJsonRepository;
import com.bankmas.report.webapi.repository.ReportTypeRepository;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.text.html.Option;

import org.junit.jupiter.api.*;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.multipart.MultipartFile;

import com.bankmas.report.webapi.dto.DataResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.bankmas.report.webapi.dto.file.UpsertReportTypeRequest;
import com.bankmas.report.webapi.dto.IdOnlyResponse;

@SpringBootTest
public class ReportTypeServiceImplTest {
    @Autowired
    private ReportTypeServiceImpl reportTypeServiceImpl;

    @MockBean
    private ReportTypeRepository reportTypeRepository;

    @MockBean
    private ReportTypeFieldJsonRepository reportTypeFieldJsonRepository;

    @Mock
    RedisTemplate<String, Serializable> redisTemplate;

    @Mock
    private ValueOperations valueOperations;

    @Test
    public void listReportType() {
        // Arrange
        List<ReportType> reportTypes = new ArrayList<>();
        when(redisTemplate.opsForValue()).thenReturn(null);
        when(reportTypeRepository.findAll(eq(Sort.by(Direction.ASC,"name")))).thenReturn(reportTypes);

        // Act
        DataResponse<List<DetailReportTypeResponse>> result = reportTypeServiceImpl.listReportType();

        // Assert
        assertEquals("SUCCESS", result.getMessage());
        assertNotNull(result.getData());
    }

    @Test
    public void getReportType() {
        // Arrange
        String id = "valid_id";

        ReportType reportType = new ReportType();
        reportType.setId(id);
        reportType.setName("valid_name");
        reportType.setTemplateFile("valid_templateFile");
        reportType.setCreatedDatetime(LocalDateTime.now());
        reportType.setUpdatedDatetime(LocalDateTime.now());
        reportType.setFieldJsons(new ArrayList<>());

        when(reportTypeRepository.findById(eq(id))).thenReturn(Optional.of(reportType));
        // Act
        DataResponse<DetailReportTypeResponse> response = reportTypeServiceImpl.getReportType(id);

        // Assert
        assertNotNull(response);
        assertEquals("SUCCESS", response.getMessage());
        assertNotNull(response.getData());
    }

    @Test
    public void createReportType() throws JsonProcessingException {
		// Arrange
		UpsertReportTypeRequest request = new UpsertReportTypeRequest();
		request.setName("Test Report Type");
		List<UpsertReportTypeRequest.JsonField> jsonFields = new ArrayList<>();
		UpsertReportTypeRequest.JsonField jsonField = new UpsertReportTypeRequest.JsonField();
		jsonField.setName("Field 1");
		jsonField.setType(EnumReportTypeFieldJsonType.TEXT);
		jsonFields.add(jsonField);
		request.setJsonFields(jsonFields);

		ReportType reportType = new ReportType();
        reportType.setId("valid_id");
        reportType.setName("Test Report Type");
        reportType.setTemplateFile("valid_templateFile");
        reportType.setCreatedDatetime(LocalDateTime.now());
        reportType.setUpdatedDatetime(LocalDateTime.now());
        reportType.setFieldJsons(new ArrayList<>());

		ReportTypeFieldJson reportTypeFieldJson = new ReportTypeFieldJson();
		reportTypeFieldJson.setId("valid_id");
		reportTypeFieldJson.setName("Field 1");
		reportTypeFieldJson.setType(EnumReportTypeFieldJsonType.TEXT);

		when(reportTypeRepository.save(any(ReportType.class))).thenReturn(reportType);
		when(reportTypeFieldJsonRepository.save(any(ReportTypeFieldJson.class))).thenReturn(reportTypeFieldJson);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(anyString(), anyString());

		// Act
		DataResponse<IdOnlyResponse> response = reportTypeServiceImpl.createReportType(request);

		// Assert
		assertEquals("SUCCESS", response.getMessage());
		assertNotNull(response.getData());
		assertNotNull(response.getData().getId());
    }

    @Test
    public void updateReportType() throws JsonProcessingException {
		// Arrange
		UpsertReportTypeRequest request = new UpsertReportTypeRequest();
		request.setName("Test Report Type");
		List<UpsertReportTypeRequest.JsonField> jsonFields = new ArrayList<>();
		UpsertReportTypeRequest.JsonField jsonField = new UpsertReportTypeRequest.JsonField();
		jsonField.setName("Field 1");
		jsonField.setType(EnumReportTypeFieldJsonType.TEXT);
		jsonFields.add(jsonField);
		request.setJsonFields(jsonFields);

		ReportType reportType = new ReportType();
        reportType.setId("valid_id");
        reportType.setName("Test Report Type");
        reportType.setTemplateFile("valid_templateFile");
        reportType.setCreatedDatetime(LocalDateTime.now());
        reportType.setUpdatedDatetime(LocalDateTime.now());
        reportType.setFieldJsons(new ArrayList<>());

		ReportTypeFieldJson reportTypeFieldJson = new ReportTypeFieldJson();
		reportTypeFieldJson.setId("valid_id");
		reportTypeFieldJson.setName("Field 1");
		reportTypeFieldJson.setType(EnumReportTypeFieldJsonType.TEXT);

		when(reportTypeRepository.save(any(ReportType.class))).thenReturn(reportType);
		when(reportTypeFieldJsonRepository.save(any(ReportTypeFieldJson.class))).thenReturn(reportTypeFieldJson);
        doNothing().when(reportTypeFieldJsonRepository).delete(any());;
        when(reportTypeRepository.findById(eq("valid_id"))).thenReturn(Optional.of(reportType));
        when(reportTypeRepository.findFirstByNameAndIdNot(anyString(), anyString())).thenReturn(Optional.empty());
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(anyString(), anyString());

		// Act
		DataResponse<IdOnlyResponse> response = reportTypeServiceImpl.updateReportType("valid_id",request);

		// Assert
		assertEquals("SUCCESS", response.getMessage());
		assertNotNull(response.getData());
		assertNotNull(response.getData().getId());
    }
}
