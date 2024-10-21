package blog.posts.p1.authentication.userManagement.controller;

import blog.posts.p1.authentication.config.JwtService;
import blog.posts.p1.authentication.config.TelegramBotComponent;
import blog.posts.p1.authentication.userManagement.dto.Request.TelegramAuthRequest;
import blog.posts.p1.authentication.userManagement.dto.Response.LoginResponseDto;
import blog.posts.p1.authentication.userManagement.entity.User;
import blog.posts.p1.authentication.userManagement.mapper.UserMapper;
import blog.posts.p1.authentication.userManagement.repository.UserRepository;
import blog.posts.p1.authentication.userManagement.services.AuthService;
import blog.posts.p1.authentication.userManagement.services.TelegramAuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth/telegram")
public class TelegramAuthController {
    private final TelegramAuthService telegramAuthService;
    private final JwtService jwtService;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final TelegramBotComponent telegramBotComponent;

    @PostMapping("/login")
    public ResponseEntity<?> telegramLogin(@RequestBody TelegramAuthRequest request) {
        if (telegramAuthService.verifyTelegramAuth(request)) {
            User user = telegramAuthService.createOrUpdateUser(request);
            String token = jwtService.generateToken(user, new HashMap<>());
            String refreshToken = authService.createRefreshToken(user).getRefreshToken();

            return ResponseEntity.ok(new LoginResponseDto(userMapper.toDto(user), token, refreshToken));
        }
        return ResponseEntity.badRequest().body("Invalid Telegram authentication data");
    }

    @GetMapping("/auth")
    public ResponseEntity<?> telegramAuth(@RequestParam String id) {
        User user = userRepository.findByTelegramId(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtService.generateToken(user, new HashMap<>());
        String refreshToken = authService.createRefreshToken(user).getRefreshToken();
        return ResponseEntity.ok(new LoginResponseDto(userMapper.toDto(user), token, refreshToken));
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody Update update) {
        log.info("Received webhook update: {}", update);
        telegramBotComponent.onWebhookUpdateReceived(update);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/webhook")
    public ResponseEntity<String> handleWebhookGet() {
        return ResponseEntity.ok("Telegram Webhook is active");
    }

}
