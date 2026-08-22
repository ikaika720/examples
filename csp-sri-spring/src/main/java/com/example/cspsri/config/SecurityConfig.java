package com.example.cspsri.config;

import com.example.cspsri.service.CspStateService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.header.HeaderWriterFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CspStateService cspStateService;

    public SecurityConfig(CspStateService cspStateService) {
        this.cspStateService = cspStateService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        CookieCsrfTokenRepository tokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();
        tokenRepository.setCookieCustomizer(builder -> builder.sameSite("Lax").secure(true));

        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            )
            // Enable CSRF Token protection with SameSite=Lax and Secure attributes
            .csrf(csrf -> csrf
                .csrfTokenRepository(tokenRepository)
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                .ignoringRequestMatchers("/api/csp-report")
            )
            // Register CspFilter in Spring Security filter chain before HeaderWriterFilter
            .addFilterBefore(new CspFilter(cspStateService), HeaderWriterFilter.class);

        return http.build();
    }
}
