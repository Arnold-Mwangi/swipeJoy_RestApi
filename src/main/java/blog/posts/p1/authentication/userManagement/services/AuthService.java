package blog.posts.p1.authentication.userManagement.services;

import blog.posts.p1.authentication.userManagement.entity.RefreshToken;
import blog.posts.p1.authentication.userManagement.entity.User;
import blog.posts.p1.authentication.userManagement.repository.RefreshTokensRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    @Value("${security.jwt.refresh_token_duration_ms}")
    private Long refreshTokenDuration;

    private final RefreshTokensRepository refreshTokensRepository;

    public AuthService(RefreshTokensRepository refreshTokensRepository) {
        this.refreshTokensRepository = refreshTokensRepository;
    }

    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setRefreshToken(UUID.randomUUID().toString());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expirationTime = now.plusDays(1).toLocalDate().atStartOfDay();

        refreshToken.setExpiresAt(expirationTime);
        return refreshTokensRepository.save(refreshToken);
    }

}
