package blog.posts.p1.authentication.userManagement.dto.Request;


import lombok.Data;

@Data
public class TokenRefreshRequestDto {
    private String refreshToken;
}
