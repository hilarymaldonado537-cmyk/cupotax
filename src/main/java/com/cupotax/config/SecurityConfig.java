package com.cupotax.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .ignoringAntMatchers("/api/**", "/login", "/register", "/logout")
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            .authorizeHttpRequests(auth -> auth
                .antMatchers("/", "/login", "/register", "/h2-console/**", "/debug/users").permitAll()
                .antMatchers("/css/**", "/js/**", "/images/**", "/img/**").permitAll()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/taxista/**").hasRole("TAXISTA")
                .antMatchers("/busero/**").hasRole("BUSERO")
                .antMatchers("/usuario/**").hasAnyRole("USUARIO", "ESTUDIANTE")
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions().disable())
            .formLogin(form -> form.disable())
            .httpBasic(httpBasic -> httpBasic.disable());
        
        return http.build();
    }
}