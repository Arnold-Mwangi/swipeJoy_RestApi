package blog.posts.p1.authentication.userManagement.repository;

import blog.posts.p1.authentication.userManagement.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByRoleOrId(String client, Long id);
}
