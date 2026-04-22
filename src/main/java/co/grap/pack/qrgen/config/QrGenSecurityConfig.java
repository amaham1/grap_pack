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
 * QR Generator 서비스 보안 설정이다.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class QrGenSecurityConfig {

    private final PasswordEncoder passwordEncoder;

    /**
     * QR Generator 사용자 인증 공급자를 등록한다.
     */
    @Bean
    public DaoAuthenticationProvider qrGenUserAuthProvider(QrGenAuthService authService) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(authService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * 인증이 필요한 QRgen 사용자 영역 보안 체인이다.
     */
    @Bean
    @Order(4)
    public SecurityFilterChain qrGenAuthenticatedFilterChain(
            HttpSecurity http,
            DaoAuthenticationProvider qrGenUserAuthProvider
    ) throws Exception {
        http
                .securityMatcher("/qrgen/user/**")
                .authenticationProvider(qrGenUserAuthProvider)
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/qrgen/user/api/**")
                )
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().hasRole("QRGEN_USER")
                )
                .formLogin(form -> form
                        .loginPage("/qrgen/auth/login")
                        .loginProcessingUrl("/qrgen/auth/login")
                        .defaultSuccessUrl("/qrgen/user/history", true)
                        .failureUrl("/qrgen/auth/login?error=true")
                        .usernameParameter("loginId")
                        .passwordParameter("password")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/qrgen/auth/logout")
                        .logoutSuccessUrl("/qrgen/?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                );

        return http.build();
    }

    /**
     * 공개 QRgen 영역 보안 체인이다.
     */
    @Bean
    @Order(5)
    public SecurityFilterChain qrGenPublicFilterChain(
            HttpSecurity http,
            DaoAuthenticationProvider qrGenUserAuthProvider
    ) throws Exception {
        http
                .securityMatcher("/qrgen/**")
                .authenticationProvider(qrGenUserAuthProvider)
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/qrgen/generate", "/qrgen/download", "/qrgen/data/verify")
                )
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form
                        .loginPage("/qrgen/auth/login")
                        .loginProcessingUrl("/qrgen/auth/login")
                        .defaultSuccessUrl("/qrgen/", false)
                        .failureUrl("/qrgen/auth/login?error=true")
                        .usernameParameter("loginId")
                        .passwordParameter("password")
                        .permitAll()
                )
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
