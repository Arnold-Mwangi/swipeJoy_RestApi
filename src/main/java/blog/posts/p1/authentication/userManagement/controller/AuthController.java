package blog.posts.p1.authentication.userManagement.controller;

import blog.posts.p1.authentication.config.JwtService;
import blog.posts.p1.authentication.exceptions.TokenRefreshException;
import blog.posts.p1.authentication.userManagement.dto.Request.TokenRefreshRequestDto;
import blog.posts.p1.authentication.userManagement.dto.Request.UserRequestDto;
import blog.posts.p1.authentication.userManagement.dto.Response.LoginResponseDto;
import blog.posts.p1.authentication.userManagement.dto.Response.TokenRefreshResponseDto;
import blog.posts.p1.authentication.userManagement.entity.RefreshToken;
import blog.posts.p1.authentication.userManagement.entity.User;
import blog.posts.p1.authentication.userManagement.repository.UserRepository;
import blog.posts.p1.authentication.userManagement.services.AuthService;
import blog.posts.p1.authentication.userManagement.services.RedisService;
import blog.posts.p1.authentication.userManagement.services.RefreshTokenService;
import blog.posts.p1.authentication.userManagement.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@CrossOrigin
@Slf4j
@RequestMapping("/auth")
@Validated
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final RedisService redisService;

    Logger logger = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserService userService, JwtService jwtService, AuthService authService, RefreshTokenService refreshTokenService, UserRepository userRepository, RedisService redisService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.userRepository = userRepository;
        this.redisService = redisService;
    }

    @Tag(name = "get", description = "GET methods of Employee APIs")
    @PostMapping("/register")
    public ResponseEntity<LoginResponseDto> register(@RequestBody UserRequestDto requestDto) {
        return ResponseEntity.ok(userService.createUSer(requestDto));
    }


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody UserRequestDto requestDto) {
        return ResponseEntity.ok(userService.authenticateUser(requestDto));
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<TokenRefreshResponseDto> refreshToken(@RequestBody TokenRefreshRequestDto requestDto, HttpServletResponse response) throws IOException {
        String requestRefreshToken = requestDto.getRefreshToken();
        logger.info("refresh token {}", requestRefreshToken);
        Optional<RefreshToken> token = refreshTokenService.findByToken(requestRefreshToken);
        log.error("expiry: {}", token);
        if(!token.isPresent()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
        }
        return token
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    String accessToken = jwtService.generateToken(user, userService.generateExtraClaims(user));
                    logger.info("new token {}", accessToken);

                    RefreshToken refreshToken = authService.createRefreshToken(user);
                    String newRefreshToken = refreshToken.getRefreshToken();
                    return ResponseEntity.ok(new TokenRefreshResponseDto(accessToken, newRefreshToken));
                })
                .orElseThrow(() -> new TokenRefreshException(requestRefreshToken, "Token does not exist"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) throws IOException {
        log.error("starting logout");
        String authHeader = request.getHeader("Authorization"); // Bearer jwt

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid or missing Authorization header");
        }
        String jwt = authHeader.substring(7);
        log.error("jwt loaded:", jwt);
        String username = jwtService.extractUsername(jwt);
        Optional<User> optionalUser = userRepository.findByEmailOrPhone(username, username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found for token");
        }
        refreshTokenService.deleteTokenByUser(Optional.of(optionalUser.get()));
        redisService.addToBlacklist(jwt, jwtService.getExpiration(jwt));
        SecurityContextHolder.clearContext();
        logger.info("User logged out: {}", username);
        return ResponseEntity.ok("Logout successful");
    }
}