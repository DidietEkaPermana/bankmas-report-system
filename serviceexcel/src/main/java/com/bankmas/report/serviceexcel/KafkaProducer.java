package com.bankmas.report.serviceexcel;

public interface KafkaProducer {
	public void generateMessage(String id, String status);
}
