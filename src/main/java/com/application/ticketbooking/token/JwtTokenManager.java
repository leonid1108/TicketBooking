package com.application.ticketbooking.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Duration;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;

/**
 * Этот класс предоставляет методы для генерации JWT токенов с информацией о пользователе и их извлечении.
 * Управляет созданием и извлечением данных из JWT.
 */
@Component
public class JwtTokenManager {

    private final Key key;

    @Value("${jwt.lifetime}")
    private Duration jstLifeTime;

    /**
     * Конструктор для инициализации JwtTokenManager с секретным ключом.
     * Ключ используется для подписывания токенов.
     *
     * @param secret Секретный ключ для подписания токенов.
     */
    public JwtTokenManager(@Value("${jwt.secret}") String secret) {
        byte[] decodedKey = Base64.getDecoder().decode(secret);
        this.key = new SecretKeySpec(decodedKey, SignatureAlgorithm.HS256.getJcaName());
    }

    /**
     * Генерирует JWT токен для пользователя, включающий его роли.
     *
     * @param userDetails объект {@link UserDetails}, содержащий информацию о пользователе.
     * @return {@link String} токен в виде строки.
     */
    public String generateJwtToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();

        claims.put("roles", roles);

        Date timeStart = new Date();
        Date timeEnd = new Date(timeStart.getTime() + jstLifeTime.toMillis());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(timeStart)
                .setExpiration(timeEnd)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Извлекает имя пользователя из JWT токена.
     *
     * @param token JWT токен в виде строки.
     * @return Имя пользователя из токена.
     */
    public String getUsername(String token) {
        return getClaimsByToken(token).getSubject();
    }

    /**
     * Извлекает полезную нагрузку из JWT токена.
     *
     * @param token JWT токен в виде строки.
     * @return Полезная нагрузка токена в виде объекта {@link Claims}.
     */
    private Claims getClaimsByToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
