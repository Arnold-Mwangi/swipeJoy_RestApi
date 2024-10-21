package blog.posts.p1.authentication.userManagement.services;

import blog.posts.p1.authentication.exceptions.TokenRefreshException;
import blog.posts.p1.authentication.userManagement.entity.RefreshToken;
import blog.posts.p1.authentication.userManagement.entity.User;
import blog.posts.p1.authentication.userManagement.repository.RefreshTokensRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class RefreshTokenService {

    private final RefreshTokensRepository refreshTokenRepository;

    @Transactional
    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByRefreshToken(token);
    }
    @Transactional
    public RefreshToken verifyExpiration(RefreshToken token) {
        LocalDateTime now = LocalDateTime.now();

        if (token.getExpiresAt().isAfter(now) || token.getExpiresAt().isEqual(now)) {
            return token;
        }else{
            refreshTokenRepository.deleteByRefreshToken(token.getRefreshToken());
            throw new TokenRefreshException(token.getRefreshToken(), "Refresh token was expired. Please make a new signin request");
        }
    }

    @Transactional
    public void deleteTokenByUser(Optional<User> user) {
        refreshTokenRepository.deleteByUser(user);
    }
}
