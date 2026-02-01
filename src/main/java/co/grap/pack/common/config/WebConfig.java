package co.grap.pack.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 설정
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${qrgen.qr-image.save-path:${user.dir}/uploads/qrgen/images}")
    private String qrgenImagePath;

    /**
     * 정적 리소스 핸들러 설정
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 업로드된 파일 접근 경로 설정
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");

        // QR Generator 이미지 접근 경로 설정
        registry.addResourceHandler("/qrgen/images/**")
                .addResourceLocations("file:" + qrgenImagePath + "/");
    }
}
