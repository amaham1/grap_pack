package co.grap.pack.qrgen.config;

import co.grap.pack.qrgen.auth.service.QrGenAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * QR Generator 서비스 Spring Security 설정
 * 익명 사용자와 로그인 사용자 모두 QR 생성 가능
 * 로그인 사용자만 히스토리 조회 가능
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class QrGenSecurityConfig {
    
    private final PasswordEncoder passwordEncoder;

    /**
     * QR Generator 사용자 인증 제공자
     */
    @Bean
    public DaoAuthenticationProvider qrGenUserAuthProvider(QrGenAuthService authService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(authService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * 인증 필요 영역 Security 필터 체인 (/qrgen/user/**)
     * 히스토리 조회 등 로그인 필요 기능
     */
    @Bean
    @Order(4)
    public SecurityFilterChain qrGenAuthenticatedFilterChain(HttpSecurity http,
            DaoAuthenticationProvider qrGenUserAuthProvider) throws Exception {
        http
            .securityMatcher("/qrgen/user/**")
            .authenticationProvider(qrGenUserAuthProvider)
            // CSRF 설정
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/qrgen/user/api/**")
            )
            // 인가 설정
            .authorizeHttpRequests(auth -> auth
                .anyRequest().hasRole("QRGEN_USER")
            )
            // 로그인 설정
            .formLogin(form -> form
                .loginPage("/qrgen/auth/login")
                .loginProcessingUrl("/qrgen/auth/login")
                .defaultSuccessUrl("/qrgen/user/history", true)
                .failureUrl("/qrgen/auth/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
            )
            // 로그아웃 설정
            .logout(logout -> logout
                .logoutUrl("/qrgen/auth/logout")
                .logoutSuccessUrl("/qrgen/?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            // 세션 설정
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            );

        return http.build();
    }

    /**
     * 공개 영역 Security 필터 체인 (/qrgen/**)
     * 익명 사용자도 접근 가능, 로그인 사용자 정보는 선택적으로 획득
     */
    @Bean
    @Order(5)
    public SecurityFilterChain qrGenPublicFilterChain(HttpSecurity http,
            DaoAuthenticationProvider qrGenUserAuthProvider) throws Exception {
        http
            .securityMatcher("/qrgen/**")
            .authenticationProvider(qrGenUserAuthProvider)
            // CSRF 설정 - generate API는 예외
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/qrgen/generate", "/qrgen/download")
            )
            // 모든 요청 허용 (익명 + 로그인 모두)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            // 로그인 설정 (공개 영역에서도 로그인 처리 가능하도록)
            .formLogin(form -> form
                .loginPage("/qrgen/auth/login")
                .loginProcessingUrl("/qrgen/auth/login")
                .defaultSuccessUrl("/qrgen/", false)
                .failureUrl("/qrgen/auth/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
            )
            // 로그아웃 설정
            .logout(logout -> logout
                .logoutUrl("/qrgen/auth/logout")
                .logoutSuccessUrl("/qrgen/?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}
