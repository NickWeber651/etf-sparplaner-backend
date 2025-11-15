package de.htw.berlin.webtech.etf.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Globale CORS-Konfiguration:
 * Erlaubt dem Frontend (Vite: localhost:5173, optiona  l GitHub Pages),
 * Requests ans Backend zu schicken. So musst du nicht an jedem Controller @CrossOrigin setzen.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer{

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**") // gilt für alle Endpunkte
                .allowedOrigins(
                        "http://localhost:5173",          // Vite Dev
                        "https://nickweber651.github.io",  // optional: GitHub Pages
                        "https://etf-sparplaner-fronted.onrender.com" // Render-Frontend
                )
                .allowedMethods("GET","HEAD","POST","PUT","PATCH","DELETE","OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(false);
    }

}
