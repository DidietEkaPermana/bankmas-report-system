package com.bankmas.report.webapi.service;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import com.bankmas.report.webapi.repository.UploadFileRepository;
import com.bankmas.report.webapi.service.file.FileServiceImpl;
import com.bankmas.report.webapi.service.kafka.KafkaProducer;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {FileServiceImpl.class})
public class FileServiceImplTest {

    @InjectMocks
    private FileServiceImpl fileService;

    @Mock
    private UploadFileRepository fileRepository;

    @Mock
    private KafkaProducer kafkaProducer;

    String content = "[]";
    

    // Saves a file successfully when all parameters are valid
    
}
