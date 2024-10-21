package blog.posts.p1.authentication.userManagement.dto.Request;

import lombok.Data;

@Data
public class TelegramAuthRequest {
    private String id;
    private String firstName;
    private String lastName;
    private String username;
    private String photoUrl;
    private String authDate;
    private String hash;
}
