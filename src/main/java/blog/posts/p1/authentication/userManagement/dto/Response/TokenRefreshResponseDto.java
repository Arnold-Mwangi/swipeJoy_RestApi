package blog.posts.p1.authentication.userManagement.dto.Response;

import blog.posts.p1.authentication.userManagement.entity.RefreshToken;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenRefreshResponseDto {
    private String accessToken;
    private String refreshToken;

    public TokenRefreshResponseDto(String token, RefreshToken requestRefreshToken) {
    }
}
