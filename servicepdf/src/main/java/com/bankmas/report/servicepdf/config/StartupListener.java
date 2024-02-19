package com.bankmas.report.servicepdf.config;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {

	
	@Override
	public final void onApplicationEvent(ContextRefreshedEvent event) {
		System.out.println("RUNNING SERVICE CSV OK .....................");
	}

}