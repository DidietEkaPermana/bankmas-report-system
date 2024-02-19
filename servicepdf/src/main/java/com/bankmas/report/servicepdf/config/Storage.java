package com.bankmas.report.servicepdf.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.bankmas.report.servicepdf.storage.StorageService;

@Configuration
@EnableConfigurationProperties(StorageProperties.class)
public class Storage {

	// this will clean up uploaded file directory, please comment if you with to retain the files
    @Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			//storageService.deleteAll();
			storageService.init();
		};
	}
}
