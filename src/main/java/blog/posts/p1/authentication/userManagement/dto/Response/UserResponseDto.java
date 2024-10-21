package blog.posts.p1.authentication.userManagement.dto.Response;

import blog.posts.p1.authentication.userManagement.entity.Role;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Boolean emailVerified;
    private Boolean phoneVerified;
    private Role role;
    private LocalDateTime createdAt;
}
