package com.module5.team2.security;

import com.module5.team2.exception.CustomAccessDeniedHandler;
import com.module5.team2.exception.CustomAuthenticationEntryPoint;
import com.module5.team2.security.jwt.JwtAuthenticationEntryPoint;
import com.module5.team2.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;
    private final JwtAuthenticationEntryPoint entryPoint;

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomAuthenticationEntryPoint customAuthenticationEntryPoint, CustomAccessDeniedHandler customAccessDeniedHandler)
            throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .exceptionHandling(e ->
                        e.authenticationEntryPoint(entryPoint)
                        .authenticationEntryPoint(customAuthenticationEntryPoint).accessDeniedHandler(customAccessDeniedHandler)
                )
                .sessionManagement(s ->
                        s.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth

                        // PUBLIC
//                        .requestMatchers(
//                                "/auth/**",
//                                "/forgot-password",
//                                "/reset-password"
//                        ).permitAll()

                        // ADMIN
//                        .requestMatchers("/admin/**")
//                        .hasRole("ADMIN")

                        // STAFF
//                        .requestMatchers("/staff/**")
//                        .hasAnyRole("STAFF", "ADMIN")

                        // Supplier
//                        .requestMatchers("/Supplier/**")
//                        .hasRole("SUPPLIER")

                        // CUSTOMER
//                        .requestMatchers("/customer/**")
//                        .hasRole("CUSTOMER")

//                        .anyRequest().authenticated()

                                // ===== PUBLIC =====
                                .requestMatchers(
                                        "/api/public/**","http://127.0.0.1:5500/Fe/Pages/Others/**"
                                ).permitAll()

                                // ===== ADMIN =====
                                .requestMatchers("/api/admin/login","http://127.0.0.1:5500/Fe/Pages/Admin/**").permitAll()
                                .requestMatchers("/api/admin/**")
                                .hasRole("ADMIN")

                                // ===== STAFF =====
                                .requestMatchers("/api/staff/**")
                                .hasAnyRole("STAFF")

                                // ===== USER LOGIN RỒI (customer / supplier / staff / admin) =====
                                .requestMatchers("/api/user/**")
                                .authenticated()

                                // ===== CÒN LẠI =====
                                .anyRequest().authenticated()
                );

        http.addFilterBefore(
                jwtFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

