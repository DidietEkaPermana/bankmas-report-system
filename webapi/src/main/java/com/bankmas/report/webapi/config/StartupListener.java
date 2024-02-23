package com.bankmas.report.webapi.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {
	
	@Override
	public final void onApplicationEvent(ContextRefreshedEvent event) {
		System.out.println("WEB API : SUDAH JALAN #########");
	}

}