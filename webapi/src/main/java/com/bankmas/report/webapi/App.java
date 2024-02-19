package com.bankmas.report.webapi;

import java.io.File;

import javax.annotation.Resource;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@SpringBootApplication
//@EnableCaching
public class App 
{
	private static final String TEMP_DIRECTORY = "export.directory";
	
	public static void main(String[] args) {
		SpringApplication.run(App.class, args);
	}
	
	@Component
    public static class Runner implements ApplicationRunner {

		@Resource
		private Environment env;
		
        @Override
        public void run(ApplicationArguments args) throws Exception {
        	String path = env.getRequiredProperty(TEMP_DIRECTORY);
        	
        	File tempFolder = new File(path + "/Temp");
            if (!tempFolder.exists()) {
                tempFolder.mkdir();
            }
        }
    }
}
