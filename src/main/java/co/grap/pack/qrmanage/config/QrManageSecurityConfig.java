package co.grap.pack.qrmanage.config;

import co.grap.pack.qrmanage.superadmin.auth.service.QrManageSuperAuthService;
import co.grap.pack.qrmanage.shopadmin.auth.service.QrManageShopAuthService;
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
 * QR 관리 서비스 Spring Security 설정
 * 최고관리자와 상점관리자 분리 인증 체계
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class QrManageSecurityConfig {

    private final PasswordEncoder passwordEncoder;

    /**
     * 최고관리자용 인증 제공자
     */
    @Bean
    public DaoAuthenticationProvider qrManageSuperAdminAuthProvider(QrManageSuperAuthService superAuthService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(superAuthService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * 상점관리자용 인증 제공자
     */
    @Bean
    public DaoAuthenticationProvider qrManageShopAdminAuthProvider(QrManageShopAuthService shopAuthService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(shopAuthService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * 최고관리자 Security 필터 체인 설정
     */
    @Bean
    @Order(1)
    public SecurityFilterChain qrManageSuperAdminFilterChain(HttpSecurity http,
            DaoAuthenticationProvider qrManageSuperAdminAuthProvider) throws Exception {
        http
            .securityMatcher("/qr-manage/super/**")
            .authenticationProvider(qrManageSuperAdminAuthProvider)
            // CSRF 설정
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/qr-manage/super/api/**")
            )
            // 인가 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/qr-manage/super/auth/**").permitAll()
                .requestMatchers("/qr-manage/super/admin/**").hasRole("SUPER_ADMIN")
                .anyRequest().authenticated()
            )
            // 로그인 설정
            .formLogin(form -> form
                .loginPage("/qr-manage/super/auth/login")
                .loginProcessingUrl("/qr-manage/super/auth/login")
                .defaultSuccessUrl("/qr-manage/super/admin/dashboard", true)
                .failureUrl("/qr-manage/super/auth/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
            )
            // 로그아웃 설정
            .logout(logout -> logout
                .logoutUrl("/qr-manage/super/auth/logout")
                .logoutSuccessUrl("/qr-manage/super/auth/login?logout=true")
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
     * 상점관리자 Security 필터 체인 설정
     */
    @Bean
    @Order(2)
    public SecurityFilterChain qrManageShopAdminFilterChain(HttpSecurity http,
            DaoAuthenticationProvider qrManageShopAdminAuthProvider) throws Exception {
        http
            .securityMatcher("/qr-manage/shop/**")
            .authenticationProvider(qrManageShopAdminAuthProvider)
            // CSRF 설정
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/qr-manage/shop/api/**")
            )
            // 인가 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/qr-manage/shop/auth/**").permitAll()
                .requestMatchers("/qr-manage/shop/admin/**").hasRole("SHOP_ADMIN")
                .anyRequest().authenticated()
            )
            // 로그인 설정
            .formLogin(form -> form
                .loginPage("/qr-manage/shop/auth/login")
                .loginProcessingUrl("/qr-manage/shop/auth/login")
                .defaultSuccessUrl("/qr-manage/shop/admin/dashboard", true)
                .failureUrl("/qr-manage/shop/auth/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
            )
            // 로그아웃 설정
            .logout(logout -> logout
                .logoutUrl("/qr-manage/shop/auth/logout")
                .logoutSuccessUrl("/qr-manage/shop/auth/login?logout=true")
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
     * 고객용 (비로그인) 및 공통 리소스 Security 필터 체인 설정
     */
    @Bean
    @Order(3)
    public SecurityFilterChain qrManagePublicFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/qr-manage/view/**", "/qr-manage/css/**", "/qr-manage/js/**", "/qr-manage/images/**")
            // CSRF 비활성화 (조회만 하므로)
            .csrf(csrf -> csrf.disable())
            // 모든 요청 허용
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );

        return http.build();
    }
}
