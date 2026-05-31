package co.grap.pack.grap.external.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * 운영 중 일회성 전체 이력 적재를 쉽게 실행하기 위한 부트스트랩 러너.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "grap.real-estate.bootstrap", havingValue = "true")
public class CmsJejuRealEstateBootstrapRunner implements ApplicationRunner {

    private final CmsJejuRealEstateApiService realEstateApiService;
    private final ConfigurableApplicationContext applicationContext;

    @Override
    public void run(ApplicationArguments args) {
        int exitCode = 0;

        try {
            log.info("제주 부동산 전체 이력 bootstrap runner 시작");
            realEstateApiService.bootstrapAllHistory();
            log.info("제주 부동산 전체 이력 bootstrap runner 완료");
        } catch (Exception exception) {
            exitCode = 1;
            log.error("제주 부동산 전체 이력 bootstrap runner 실패", exception);
        }

        final int finalExitCode = exitCode;
        SpringApplication.exit(applicationContext, () -> finalExitCode);
    }
}
