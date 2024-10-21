package blog.posts.p1.authentication.userManagement.repository;

import blog.posts.p1.authentication.userManagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String userName);

    Optional<User> findByPhone(String phone);

    Optional<User> findByEmailOrPhone(String username, String username1);

    Optional<User> findByTelegramId(String id);
}
