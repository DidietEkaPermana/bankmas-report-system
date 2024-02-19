package com.bankmas.report.webapi.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {
    /**
	 * Folder location for storing files
	 */
	private String location = "../file-repository";

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}   
}
