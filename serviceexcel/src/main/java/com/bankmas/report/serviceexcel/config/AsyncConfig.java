package com.bankmas.report.serviceexcel.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {
  
  @Bean(name="taskExecutor")
  public Executor executor(){
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(20);
    executor.setQueueCapacity(1000000);
    executor.setThreadNamePrefix("user-thread-");
    executor.initialize();
    return executor;
  }
  
}
