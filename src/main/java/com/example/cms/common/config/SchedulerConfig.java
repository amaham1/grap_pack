package com.example.cms.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 스케줄러 설정
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {
    // @EnableScheduling 어노테이션으로 스케줄링 활성화
}
