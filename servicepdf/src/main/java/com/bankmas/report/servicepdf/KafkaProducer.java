package com.bankmas.report.servicepdf;

public interface KafkaProducer {
	public void generateMessage(String id, String status);
}
