package com.application.ticketbooking.token;

import com.application.ticketbooking.security.CustomUserDetails;
import com.application.ticketbooking.service.Impl.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

/**
 * Фильтр для обработки запросов с JWT токеном.
 */
@Component
@RequiredArgsConstructor
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtTokenManager jwtTokenManager;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * Метод фильтрации запроса, который проверяет наличие и валидность JWT токена в заголовке авторизации.
     * Если токен действителен, в контексте безопасности Spring устанавливается аутентификация для пользователя,
     * указавшего токен.
     *
     * @param request {@link HttpServletRequest} HTTP запрос.
     * @param response {@link HttpServletResponse} HTTP ответ.
     * @param filterChain {@link FilterChain} цепочка фильтров.
     * @throws ServletException в случае ошибки при обработке запроса.
     * @throws IOException при возникновении ошибок ввода-вывода.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(AUTHORIZATION);
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String jwt = authorization.substring(7);
            try {
                String username = jwtTokenManager.getUsername(jwt);
                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    CustomUserDetails customUserDetails = (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);

                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            customUserDetails.getUser(),
                            null,
                            customUserDetails.getAuthorities()
                    );
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            } catch (MalformedJwtException | ExpiredJwtException e) {
                logger.debug(e.getMessage());
            }
        }
        filterChain.doFilter(request, response);
    }
}
