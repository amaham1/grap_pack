package co.grap.pack.qrgen.config;

import co.grap.pack.qrgen.visitor.interceptor.QrGenVisitorInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * QRgen 서비스 WebMvc 설정
 */
@Configuration
@RequiredArgsConstructor
public class QrGenWebMvcConfig implements WebMvcConfigurer {

    private final QrGenVisitorInterceptor qrGenVisitorInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(qrGenVisitorInterceptor)
                .addPathPatterns("/qrgen/**")
                .excludePathPatterns(
                        "/qrgen/css/**",
                        "/qrgen/js/**",
                        "/qrgen/images/**",
                        "/qrgen/api/**",
                        "/qrgen/visitor/**"
                );
    }
}
