package blog.posts.p1.authentication.config;

import blog.posts.p1.authentication.userManagement.entity.User;
import blog.posts.p1.authentication.userManagement.repository.UserRepository;
import blog.posts.p1.authentication.userManagement.services.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RedisService redisService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. Obtain the header that contains the JWT
        String authHeader = request.getHeader("Authorization"); // Bearer jwt

        if (authHeader == null || !authHeader.startsWith("Bearer")) {
            // Handle non-protected routes
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Obtain the JWT token
        String jwt = authHeader.split(" ")[1];

        // 3. Validate the JWT token
        if (!jwtService.validateJwtToken(jwt)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;
        }
        if (redisService.isBlacklisted(jwt)) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is blacklisted");
            return;
        }

        // 4. Check if the token is expired
        try {
            if (jwtService.isTokenExpired(jwt)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
                return;
            }
        } catch (Exception ex) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
            return;
        }

        // 5. Obtain the username from the JWT
        String username = jwtService.extractUsername(jwt);

        // 6. Fetch the user details and set the authentication object inside our security context
        Optional<User> optionalUser = userRepository.findByEmailOrPhone(username, username);
        if (optionalUser.isEmpty()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
            return;
        }

        User user = optionalUser.get();
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                username, null, user.getAuthorities()
        );

        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 7. Execute the remaining filters
        filterChain.doFilter(request, response);
    }
}
