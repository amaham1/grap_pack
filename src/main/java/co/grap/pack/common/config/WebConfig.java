package co.grap.pack.common.config;

import co.grap.pack.common.visitor.interceptor.PackVisitorInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 설정
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final PackVisitorInterceptor packVisitorInterceptor;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${qrgen.qr-image.save-path:${user.dir}/uploads/qrgen/images}")
    private String qrgenImagePath;

    /**
     * 정적 리소스 핸들러 설정
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadDir + "/");

        registry.addResourceHandler("/qrgen/images/**")
                .addResourceLocations("file:" + qrgenImagePath + "/");
    }

    /**
     * 공통 방문자 인터셉터 등록
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(packVisitorInterceptor)
                .addPathPatterns(
                        "/",
                        "/grap/user/**",
                        "/qrgen/**",
                        "/qr-manage/view/**"
                );
    }
}
