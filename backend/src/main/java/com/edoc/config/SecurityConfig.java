package com.edoc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.edoc.security.JwtAuthenticationFilter;
import com.edoc.security.JwtAuthenticationEntryPoint;
import com.edoc.services.JwtService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public SecurityConfig(
            UserDetailsService userDetailsService,
            JwtService jwtService,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint
    ) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAuthFilter = new JwtAuthenticationFilter(jwtService, userDetailsService);
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
              .requestMatchers("/api/confirm-email", "/api/confirm-email/").permitAll()
              .requestMatchers("/api/verify-token", "/api/verify-token/").permitAll()
              .requestMatchers("/api/register/candidat", "/api/register/candidat/").permitAll()
              .requestMatchers("/api/get-user-info","/api/get-user-info/").permitAll()
              .requestMatchers("/api/request-password-reset","/api/request-password-reset/").permitAll()
              .requestMatchers("/api/perform-password-reset","/api/perform-password-reset/").permitAll()
              .requestMatchers("/api/candidat-info","/api/candidat-info/").permitAll()
              .requestMatchers("/api/token", "/api/token/").permitAll()
              .requestMatchers("/api/login_scolarite", "/api/login_scolarite/").permitAll()
              .requestMatchers("/api/verify-is-prof", "/api/verify-is-prof/").permitAll()
              .requestMatchers("/api/token/refresh", "/api/token/refresh/").permitAll()
              .requestMatchers("/api/test/**").permitAll()
              .requestMatchers("/error").permitAll()
              .requestMatchers("/uploads/**").permitAll()
              .anyRequest().authenticated()
            )
            // Change from STATELESS to IF_REQUIRED
            .sessionManagement(session -> 
                session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            // Add session authentication strategy
            .sessionManagement(session ->
                session.sessionAuthenticationStrategy(sessionAuthenticationStrategy())
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:4200");
        configuration.addAllowedMethod("*");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(false);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        authProvider.setHideUserNotFoundExceptions(false);  
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}