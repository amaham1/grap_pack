package co.grap.pack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Grap Pack 애플리케이션 메인 클래스
 * 멀티 서비스 지원: /grap (CMS), /qr-manage (향후)
 */
@SpringBootApplication
public class GrapPackApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrapPackApplication.class, args);
    }

}
