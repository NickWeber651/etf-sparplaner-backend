package de.htw.berlin.webtech.etf.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security Konfiguration.
 * Definiert welche Endpoints geschuetzt sind und aktiviert JWT-Authentifizierung.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF deaktivieren (nicht noetig bei JWT)
            .csrf(csrf -> csrf.disable())

            // CORS aktivieren
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Session-Management: Stateless (kein Session-Cookie, nur JWT)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Endpoint-Berechtigungen definieren
            .authorizeHttpRequests(auth -> auth
                // Oeffentliche Endpoints (ohne Login erreichbar)
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()

                // Alle anderen Endpoints erfordern Authentifizierung
                .anyRequest().authenticated()
            )

            // Bei fehlender/ungueltiger Authentifizierung: 401 Unauthorized
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(401);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Nicht authentifiziert\"}");
                })
            )

            // H2-Console: Frame-Options deaktivieren (nur fuer Entwicklung)
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))

            // JWT-Filter vor dem Standard-Auth-Filter einfuegen
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Password-Encoder fuer sichere Passwort-Hashes.
     * BCrypt ist der Standard und sehr sicher.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * CORS-Konfiguration fuer Frontend-Zugriff.
     * Erlaubt Anfragen vom Vue.js Frontend (localhost:5173).
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Erlaubte Origins (Frontend-URLs)
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",                          // Vite Dev Server
            "https://nickweber651.github.io",                 // GitHub Pages
            "https://etf-sparplaner-fronted.onrender.com",
                "https://etf-sparplaner-fronted-fi2c.onrender.com"// Render Frontend
        ));

        // Erlaubte HTTP-Methoden
        configuration.setAllowedMethods(Arrays.asList(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));

        // Erlaubte Headers
        configuration.setAllowedHeaders(List.of("*"));

        // Credentials erlauben (fuer Cookies/Auth-Header)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}

