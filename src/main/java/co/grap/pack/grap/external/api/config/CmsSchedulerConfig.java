package co.grap.pack.grap.external.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Grap CMS 외부 API 동기화 스케줄러 설정
 */
@Configuration
@EnableScheduling
public class CmsSchedulerConfig {
    // @EnableScheduling 어노테이션으로 스케줄링 활성화
    // CmsExternalApiScheduler에서 @Scheduled 어노테이션으로 스케줄 작업 정의
}
