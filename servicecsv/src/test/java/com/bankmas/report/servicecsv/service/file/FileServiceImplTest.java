package com.bankmas.report.servicecsv.service.file;

import com.bankmas.report.servicecsv.dto.kafka.MessageKafkaUploadFile;
import com.bankmas.report.servicecsv.model.EnumUploadFileStatus;
import com.bankmas.report.servicecsv.service.kafka.KafkaProducer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.util.UUID;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {FileServiceImpl.class})
public class FileServiceImplTest {
	@InjectMocks
	private FileServiceImpl fileService;

	@Mock
	private KafkaProducer kafkaProducer;

	
}

