package com.bankmas.report.serviceexcel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig {
  
  @Bean(name="taskExecutor")
  public Executor executor(){
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(20);
    executor.setQueueCapacity(10000);
    executor.setThreadNamePrefix("serviceexcel-thread-");
    executor.initialize();
    return executor;
  }
  
}