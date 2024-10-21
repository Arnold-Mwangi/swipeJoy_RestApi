package blog.posts.p1.authentication.exceptions.cronJobs;

import blog.posts.p1.authentication.userManagement.repository.RefreshTokensRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RevokeRefreshTokens {
    private final RefreshTokensRepository refreshTokensRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void revokeRefreshTokens() {
        LocalDateTime now = LocalDateTime.now();
        int pageSize = 500;
        int pageNumber = 0;

        while (true) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            var expiredTokensPage = refreshTokensRepository.findByExpiresAtBefore(now, pageable);
            if (expiredTokensPage.hasContent()) {
                refreshTokensRepository.deleteAll(expiredTokensPage.getContent());
                System.out.println("Expired refresh tokens revoked: " + expiredTokensPage.getContent().size());
            } else {
                break;
            }
            pageNumber++;
        }
    }
}
