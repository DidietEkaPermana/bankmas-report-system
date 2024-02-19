package com.bankmas.report.servicecsv;

public interface KafkaProducer {
	public void generateMessage(String id, String status);
}
