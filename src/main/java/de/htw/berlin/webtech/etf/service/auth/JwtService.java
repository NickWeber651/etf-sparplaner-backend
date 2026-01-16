package de.htw.berlin.webtech.etf.service.auth;

import de.htw.berlin.webtech.etf.domain.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT Service - Erstellt und validiert JSON Web Tokens.
 *
 * Token enthaelt: userId, email, Ablaufzeit
 * Token-Gueltigkeit: 24 Stunden
 */
@Service
public class JwtService {

    // Secret Key aus application.properties (oder Fallback fuer Entwicklung)
    @Value("${jwt.secret}")
    private String secretKey;

    // Token-Gueltigkeit: 24 Stunden in Millisekunden
    private static final long EXPIRATION_TIME = 24 * 60 * 60 * 1000;

    /**
     * Erstellt einen JWT-Token fuer einen User nach erfolgreichem Login.
     *
     * @param user Der eingeloggte User
     * @return JWT-Token als String
     */
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
                .subject(user.getId().toString())           // User-ID als Subject
                .claim("email", user.getEmail())            // Email als zusaetzliche Info
                .issuedAt(now)                              // Erstellungszeitpunkt
                .expiration(expiryDate)                     // Ablaufzeitpunkt (24h)
                .signWith(getSigningKey())                  // Signatur mit Secret Key
                .compact();
    }

    /**
     * Extrahiert die User-ID aus einem Token.
     *
     * @param token Der JWT-Token
     * @return User-ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return Long.parseLong(claims.getSubject());
    }

    /**
     * Extrahiert die Email aus einem Token.
     *
     * @param token Der JWT-Token
     * @return Email-Adresse
     */
    public String getEmailFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.get("email", String.class);
    }

    /**
     * Prueft ob ein Token gueltig ist (nicht abgelaufen, richtige Signatur).
     *
     * @param token Der JWT-Token
     * @return true wenn gueltig
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Parsed den Token und gibt die Claims zurueck.
     * Wirft Exception wenn Token ungueltig.
     */
    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Erstellt den Signing Key aus dem Secret.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}

