package de.htw.berlin.webtech.etf.security;

import de.htw.berlin.webtech.etf.service.auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filter der bei jeder Anfrage den JWT-Token prueft.
 * Liest den Token aus dem Authorization-Header und validiert ihn.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Authorization Header auslesen
        String authHeader = request.getHeader("Authorization");

        // Pruefen ob Header vorhanden und mit "Bearer " beginnt
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Token extrahieren (ohne "Bearer " Prefix)
        String token = authHeader.substring(7);

        // Token validieren
        if (jwtService.validateToken(token)) {
            Long userId = jwtService.getUserIdFromToken(token);
            String email = jwtService.getEmailFromToken(token);

            // Authentication-Objekt erstellen und im SecurityContext setzen
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userId,          // Principal (User-ID)
                            email,           // Credentials (Email)
                            Collections.emptyList()  // Authorities (leer fuer jetzt)
                    );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}

