package de.htw.berlin.webtech.etf.security;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class SecurityConfigTest {

    private final SecurityConfig securityConfig = new SecurityConfig(mock(JwtAuthenticationFilter.class));

    /**
     * Test fuer passwordEncoder Methode
     */
    @Test
    void passwordEncoder_ShouldReturnBCryptPasswordEncoder() {
        // when
        PasswordEncoder encoder = securityConfig.passwordEncoder();

        // then
        assertThat(encoder).isNotNull();
        assertThat(encoder).isInstanceOf(BCryptPasswordEncoder.class);
    }

    /**
     * Zusaetzlicher Test fuer passwordEncoder - Funktionalitaet pruefen
     */
    @Test
    void passwordEncoder_ShouldEncodePasswordCorrectly() {
        // given
        PasswordEncoder encoder = securityConfig.passwordEncoder();
        String rawPassword = "testPassword123";

        // when
        String encodedPassword = encoder.encode(rawPassword);

        // then
        assertThat(encodedPassword).isNotEqualTo(rawPassword);
        assertThat(encoder.matches(rawPassword, encodedPassword)).isTrue();
        assertThat(encoder.matches("wrongPassword", encodedPassword)).isFalse();
    }

    /**
     * Test fuer corsConfigurationSource Methode - Allowed Origins
     */
    @Test
    void corsConfigurationSource_ShouldConfigureAllowedOrigins() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        CorsConfiguration config = source.getCorsConfiguration(request);

        // then
        assertThat(config).isNotNull();
        assertThat(config.getAllowedOrigins()).isNotNull();
        assertThat(config.getAllowedOrigins()).containsExactlyInAnyOrder(
            "http://localhost:5173",
            "https://nickweber651.github.io",
            "https://etf-sparplaner-fronted.onrender.com",
            "https://etf-sparplaner-fronted-fi2c.onrender.com"
        );
    }

    /**
     * Test fuer corsConfigurationSource Methode - Allowed Methods
     */
    @Test
    void corsConfigurationSource_ShouldConfigureAllowedMethods() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        CorsConfiguration config = source.getCorsConfiguration(request);

        // then
        assertThat(config).isNotNull();
        assertThat(config.getAllowedMethods()).isNotNull();
        assertThat(config.getAllowedMethods()).containsExactlyInAnyOrder(
            "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        );
    }

    /**
     * Test fuer corsConfigurationSource Methode - Allowed Headers
     */
    @Test
    void corsConfigurationSource_ShouldAllowAllHeaders() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        CorsConfiguration config = source.getCorsConfiguration(request);

        // then
        assertThat(config).isNotNull();
        assertThat(config.getAllowedHeaders()).isNotNull();
        assertThat(config.getAllowedHeaders()).containsExactly("*");
    }

    /**
     * Test fuer corsConfigurationSource Methode - Allow Credentials
     */
    @Test
    void corsConfigurationSource_ShouldAllowCredentials() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();

        // when
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        CorsConfiguration config = source.getCorsConfiguration(request);

        // then
        assertThat(config).isNotNull();
        assertThat(config.getAllowCredentials()).isNotNull();
        assertThat(config.getAllowCredentials()).isTrue();
    }
}

