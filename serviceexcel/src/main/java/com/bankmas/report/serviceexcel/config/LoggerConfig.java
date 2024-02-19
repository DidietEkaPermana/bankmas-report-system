package com.bankmas.report.serviceexcel.config;

import org.springframework.context.annotation.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class LoggerConfig {
    @Bean
    public Logger logger() {
        return LoggerFactory.getLogger(getClass());
    }
}