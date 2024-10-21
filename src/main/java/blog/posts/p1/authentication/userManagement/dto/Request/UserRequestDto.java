package blog.posts.p1.authentication.userManagement.dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRequestDto {

    @Email(message = "Invalid email", regexp = "^(.+)@(.+)$")
    private String email;

    @Pattern(regexp="^\\+?[1-9]\\d{1,14}$", message="Invalid phone number")
    private String phone;

//    @Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", message="Minimum eight characters, at least one letter, one number and one special character")
    private String password;
//    @Pattern(regexp="^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$", message="Minimum eight characters, at least one letter, one number and one special character")
    private String confirmPassword;
}
