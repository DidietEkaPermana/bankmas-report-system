package com.bankmas.report.webapi.service.file;

import com.bankmas.report.webapi.dto.DataResponse;
import com.bankmas.report.webapi.dto.file.SaveFileRequest;
import com.bankmas.report.webapi.dto.file.SaveFileResponse;
import com.bankmas.report.webapi.exception.ValidationException;
import com.bankmas.report.webapi.model.EnumDocumentFileType;
import com.bankmas.report.webapi.model.ReportType;
import com.bankmas.report.webapi.model.UploadFile;
import com.bankmas.report.webapi.repository.ReportTypeRepository;
import com.bankmas.report.webapi.repository.UploadFileRepository;
import com.bankmas.report.webapi.service.kafka.KafkaProducer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.multipart.MultipartFile;
import com.bankmas.report.webapi.model.EnumUploadFileStatus;
import com.bankmas.report.webapi.dto.PaginationResponse;
import com.bankmas.report.webapi.dto.file.ListFileResponse;
import com.bankmas.report.webapi.dto.kafka.MessageKafkaUpdateStatusFile;
import com.bankmas.report.webapi.dto.file.DetailUploadFileResponse;
import com.bankmas.report.webapi.dto.IdOnlyResponse;

@SpringBootTest
public class FileServiceImplTest {
	@Autowired
	private FileServiceImpl fileService;

	@MockBean
	private UploadFileRepository uploadFileRepository;

	@MockBean
	private ReportTypeRepository reportTypeRepository;

	@MockBean
	private KafkaProducer kafkaProducer;

	@Test
	public void saveFile() throws ValidationException, IOException, NoSuchAlgorithmException {
		 // Arrange
		MultipartFile multipart = mock(MultipartFile.class);
		when(multipart.getOriginalFilename()).thenReturn("test.json");
		when(multipart.getInputStream()).thenReturn(new ByteArrayInputStream("[{\"name\":\"John\",\"age\":30}]".getBytes()));
		SaveFileRequest request = SaveFileRequest.builder().
		  documentFileType(EnumDocumentFileType.CSV).
		  file(new MultipartFile[]{ multipart }).
		  reportType("MONTHLY").
		  build();

		ReportType reportType = ReportType.builder().
		 id(UUID.randomUUID().toString()).
		 name("MONTHLY").
		 build();


		when(reportTypeRepository.findFirstByName(eq("MONTHLY"))).thenReturn(Optional.of(reportType));
		// Act
		DataResponse<List<SaveFileResponse>> response = fileService.saveFile(request);

		// Assert
		assertEquals("SUCCESS", response.getMessage());
		assertEquals(1, response.getData().size());
		SaveFileResponse saveFileResponse = response.getData().get(0);
		assertEquals("test.json", saveFileResponse.getFileName());
		assertEquals("SUCCESS", saveFileResponse.getStatus());
		assertNull(saveFileResponse.getReason());
	}

	@Test
	public void listFile() {
		// Arrange
		Integer page = 1;
		Integer size = 10;
		EnumUploadFileStatus status = EnumUploadFileStatus.SUCCESS;

		when(uploadFileRepository.findAllByStatus(eq(status), eq(PageRequest.of(page, size).withSort(Direction.DESC, "processDatetime")))).thenReturn(Page.empty(Pageable.ofSize(size)));

		// Act
		PaginationResponse<List<ListFileResponse>> response = fileService.listFile(page, size, status);

		// Assert
		assertEquals("SUCCESS", response.getMessage());
		assertNotNull(response.getData());
	}

	@Test
	public void updateStatusFile() {
		// Create a valid message with status SUCCESS
		MessageKafkaUpdateStatusFile message = new MessageKafkaUpdateStatusFile();
		message.setId("valid_id");
		message.setStatus(EnumUploadFileStatus.SUCCESS);
		message.setFinishDatetime("2022-01-01 00:00:00");
		message.setFormatedFileName("formatted_file_name");

		// Create a mock UploadFile object with the same ID as the message
		UploadFile uploadFile = new UploadFile();
		uploadFile.setId("valid_id");
		uploadFile.setStatus(EnumUploadFileStatus.UNPROCESSED);

		// Mock the fileRepository.findById() method to return the mock UploadFile object
		Mockito.when(uploadFileRepository.findById(eq("valid_id"))).thenReturn(Optional.of(uploadFile));

		// Call the updateStatusFile() method with the mock message
		fileService.updateStatusFile(message);

		// Verify that the fileRepository.save() method has been called
		Mockito.verify(uploadFileRepository).save(uploadFile);
	}

	@Test
	public void getFile() {
		String validId = "valid_id";

		// Create a mock UploadFile object with the same ID as the message
		UploadFile uploadFile = new UploadFile();
		uploadFile.setId("valid_id");
		uploadFile.setStatus(EnumUploadFileStatus.UNPROCESSED);
		uploadFile.setDocumentFileType(EnumDocumentFileType.CSV);
		uploadFile.setChecksum("checksum");
		uploadFile.setFinishDatetime(LocalDateTime.now());
		uploadFile.setFormatedFileName("formatted_file_name");
		uploadFile.setProcessDatetime(LocalDateTime.now());

		// Mock the fileRepository.findById() method to return the mock UploadFile object
		Mockito.when(uploadFileRepository.findById(eq("valid_id"))).thenReturn(Optional.of(uploadFile));

		// Act
		DataResponse<DetailUploadFileResponse> response = fileService.getFile(validId);

		// Assert
		assertEquals("SUCCESS", response.getMessage());
		assertNotNull(response.getData());
		assertEquals(DetailUploadFileResponse.class, response.getData().getClass());
	}

	@Test
	public void deleteFile() throws IOException {
		// Create a mock UploadFile object with the same ID as the message
		UploadFile uploadFile = new UploadFile();
		uploadFile.setId("valid_id");
		uploadFile.setStatus(EnumUploadFileStatus.SUCCESS);
		uploadFile.setDocumentFileType(EnumDocumentFileType.CSV);
		uploadFile.setChecksum("checksum");
		uploadFile.setFinishDatetime(LocalDateTime.now());
		uploadFile.setFormatedFileName("formatted_file_name");
		uploadFile.setProcessDatetime(LocalDateTime.now());

		when(uploadFileRepository.findById(eq("valid_id"))).thenReturn(Optional.of(uploadFile));
		doNothing().when(uploadFileRepository).deleteById(eq("valid_id"));

		// Act
		DataResponse<IdOnlyResponse> response = fileService.deleteFile("valid_id");

		// Assert
		assertEquals("SUCCESS", response.getMessage());
		assertEquals("valid_id", response.getData().getId());
	}
}
