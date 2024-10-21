package blog.posts.p1.authentication.userManagement.dto.Response;

import lombok.Data;

@Data
public class LoginResponseDto {
    private UserResponseDto user;
    private String jwt;
    private String refreshToken;

    public LoginResponseDto(UserResponseDto user, String jwt, String refreshToken) {
        this.user = user;
        this.jwt = jwt;
        this.refreshToken = refreshToken;
    }

    public UserResponseDto getUser() {
        return user;
    }

    public void setUser(UserResponseDto user) {
        this.user = user;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
