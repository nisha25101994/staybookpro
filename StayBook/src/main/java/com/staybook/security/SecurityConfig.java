package com.staybook.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity   // ✅ ADD THIS LINE

public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .httpBasic(httpBasic -> httpBasic.disable())
            .formLogin(form -> form.disable())
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

            	    // 🔓 AUTH & SYSTEM
            	    .requestMatchers("/api/auth/**").permitAll()
            	    .requestMatchers("/error").permitAll()
            	    .requestMatchers("/uploads/**").permitAll()

            	    // 🔐 VENDOR (Must be BEFORE public hotels to override /api/hotels/**)
            	    .requestMatchers("/api/hotels/vendor/**").authenticated()
            	    .requestMatchers("/api/rooms/**").authenticated()

            	    // 🔐 BOOKINGS (USER)
            	    .requestMatchers("/api/bookings/**").authenticated()

            	    // 🔓 PUBLIC USER APIs (HOTELS)
            	    // Now calling /api/hotels/vendor will NOT match here because it was caught above
            	    .requestMatchers(HttpMethod.GET, "/api/hotels/**").permitAll()
            	    
            	    // 🔐 ADMIN
            	    .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
            	    .anyRequest().authenticated()
            	)


            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
