package co.grap.pack.grap.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Grap CMS 서비스 Spring Security 설정
 */
@Configuration
@EnableWebSecurity
public class CmsSecurityConfig {

    /**
     * 비밀번호 인코더 빈 등록
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Security 필터 체인 설정
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF 설정
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
            )
            // 인가 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/grap/auth/**", "/grap/user/**", "/grap/css/**", "/grap/js/**", "/common/**", "/images/**", "/uploads/**").permitAll()
                .requestMatchers("/grap/admin/**").authenticated()
                .anyRequest().permitAll()
            )
            // 로그인 설정
            .formLogin(form -> form
                .loginPage("/grap/auth/login")
                .loginProcessingUrl("/grap/auth/login")
                .defaultSuccessUrl("/grap/admin/content/list", true)
                .failureUrl("/grap/auth/login?error=true")
                .usernameParameter("username")
                .passwordParameter("password")
                .permitAll()
            )
            // 로그아웃 설정
            .logout(logout -> logout
                .logoutUrl("/grap/auth/logout")
                .logoutSuccessUrl("/grap/auth/login?logout=true")
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
}
