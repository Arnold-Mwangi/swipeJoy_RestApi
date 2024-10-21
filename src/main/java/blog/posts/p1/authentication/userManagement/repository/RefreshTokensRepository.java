package blog.posts.p1.authentication.userManagement.repository;

import blog.posts.p1.authentication.userManagement.entity.RefreshToken;
import blog.posts.p1.authentication.userManagement.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface RefreshTokensRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String refresh_token);
    void deleteByRefreshToken(String refresh_token);
    void deleteByUser(Optional<User> user);

    Page<RefreshToken> findByExpiresAtBefore(LocalDateTime now, Pageable pageable);
}
