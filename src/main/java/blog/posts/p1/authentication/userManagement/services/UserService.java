package blog.posts.p1.authentication.userManagement.services;

import blog.posts.p1.authentication.config.JwtService;
import blog.posts.p1.authentication.exceptions.InvalidLoginException;
import blog.posts.p1.authentication.exceptions.UserAuthenticationFailed;
import blog.posts.p1.authentication.exceptions.UserInvalidInputProblem;
import blog.posts.p1.authentication.userManagement.dto.Request.UserRequestDto;
import blog.posts.p1.authentication.userManagement.dto.Response.LoginResponseDto;
import blog.posts.p1.authentication.userManagement.dto.Response.UserResponseDto;
import blog.posts.p1.authentication.userManagement.entity.Role;
import blog.posts.p1.authentication.userManagement.entity.User;
import blog.posts.p1.authentication.userManagement.mapper.UserMapper;
import blog.posts.p1.authentication.userManagement.repository.RoleRepository;
import blog.posts.p1.authentication.userManagement.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.zalando.problem.violations.Violation;

import java.util.*;
import java.util.regex.Pattern;


@Service
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final AuthService authService;
    private final RoleRepository roleRepository;

    private final Logger log = LoggerFactory.getLogger(UserService.class);

    @Transactional
    public LoginResponseDto createUSer(UserRequestDto userRequestDto) {
        List<Violation> violations = validateSignupInput(userRequestDto);
        if (!violations.isEmpty()){
            throw new UserInvalidInputProblem("Signup", violations);
        }
        User user = userMapper.toEntity(userRequestDto);
        Role role = roleRepository.findByRoleOrId("CLIENT", 3L);

        user.setRole(role);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Logger log = LoggerFactory.getLogger(UserService.class);
        log.info("user to save: {}", user.toString());

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser, generateExtraClaims(savedUser));
        String refreshToken = authService.createRefreshToken(user).getRefreshToken();

        return new LoginResponseDto(userMapper.toDto(savedUser), token, refreshToken);
    }



    public LoginResponseDto authenticateUser(UserRequestDto requestDto){
        List<Violation> violations = validateLoginInput(requestDto);
        if(!violations.isEmpty()){
            throw new UserAuthenticationFailed("Login", violations);
        }

        String username = requestDto.getEmail() != null ? requestDto.getEmail() :
                requestDto.getPhone() != null ? requestDto.getPhone() : null;

        if (username == null) {
            throw new InvalidLoginException("Email or phone number must be provided.",
                    new Violation("username", "Email or phone number is required."));
        }

        // Create authentication token
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                username, requestDto.getPassword()
        );

        try{
            authenticationManager.authenticate(authToken);
        } catch (AuthenticationException e) {
            Violation violation = new Violation("credentials", "Invalid email or password");
            throw new InvalidLoginException("Invalid login credentials", violation);
        }

        User user = userRepository.findByEmailOrPhone(username, username).get();
        UserResponseDto userResponseDto = userMapper.toDto(user);

        String jwt = jwtService.generateToken(user, generateExtraClaims(user));
        String refreshToken = authService.createRefreshToken(user).getRefreshToken();

        return new LoginResponseDto(userResponseDto, jwt, refreshToken);
    }

    public Map<String, Object> generateExtraClaims(User user){

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("identity", user.getId());
        extraClaims.put("role", user.getRole());
        // Add email and phone if they exist and are not empty
        Optional.ofNullable(user.getEmail()).filter(email -> !email.isEmpty()).ifPresent(email -> extraClaims.put("email", email));
        Optional.ofNullable(user.getPhone()).filter(phone -> !phone.isEmpty()).ifPresent(phone -> extraClaims.put("phone", phone));

        return extraClaims;

    }

    public List<Violation> validateSignupInput(UserRequestDto userRequestDto){
        List<Violation> violations = new ArrayList<>();
        if ((userRequestDto.getPassword() == null || userRequestDto.getPassword().isEmpty()) || (userRequestDto.getConfirmPassword() == null || userRequestDto.getConfirmPassword().isEmpty())) {
            violations.add( new Violation("Password", "Password can't be null"));
        }
        if(userRequestDto.getEmail() != null && !userRequestDto.getEmail().isEmpty()){
            violations.addAll(checkEmailExistence(userRequestDto.getEmail()));
        }
        if(!userRequestDto.getPhone().isEmpty() && userRequestDto.getPhone() != null){
            violations.addAll(checkPhoneExistence(userRequestDto.getPhone()));
        }
        validateEmailPhone(userRequestDto, violations);
        return violations;
    }

    private void validateEmailPhone(UserRequestDto userRequestDto, List<Violation> violations) {
        boolean emailProvided = userRequestDto.getEmail() != null && !userRequestDto.getEmail().isEmpty();
        boolean phoneProvided = userRequestDto.getPhone() != null && !userRequestDto.getPhone().isEmpty();

        if (!emailProvided && !phoneProvided) {
            violations.add(new Violation("Contact", "Either email or phone must be provided"));
        }
        if (emailProvided) {
            violations.addAll(validateEmail(userRequestDto.getEmail()));

        }
        if (phoneProvided) {
            violations.addAll(validatePhone(userRequestDto.getPhone()));
        }
    }

    public List<Violation> validateLoginInput(UserRequestDto requestDto) {
        List<Violation> violations = new ArrayList<>();

        if(requestDto.getPassword().isEmpty()){
            violations.add(new Violation("Password", "Provide your password"));
        }

        validateEmailPhone(requestDto, violations);

        return violations;
    }

    public List<Violation> validateEmail(String email) {
        List<Violation> violations = new ArrayList<>();

        String emailRegex = "^(.+)@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);

        if(!pattern.matcher(email).matches()) {
            violations.add(new Violation("Email", "Invalid email"));
        }
        return violations;
    }

    public List<Violation> checkEmailExistence(String email) {
        List<Violation> violations = new ArrayList<>();
        if(userRepository.findByEmail(email).isPresent()){
            violations.add(new Violation("Email ", "Already exists"));
        }
        return violations;
    }

    public List<Violation> validatePhone(String phone) {
        List<Violation> violations = new ArrayList<>();

        String phoneRegex = "^[+]?[0-9]{10,13}$";
        Pattern pattern = Pattern.compile(phoneRegex);

        if (!pattern.matcher(phone).matches()) {
            violations.add(new Violation("Phone", "Invalid phone number"));
        }
        return violations;
    }

    public List<Violation> checkPhoneExistence(String phone) {
        List<Violation> violations = new ArrayList<>();
        if (userRepository.findByPhone(phone).isPresent()) {
            violations.add(new Violation("Phone", "Already exists"));
        }
        return violations;
    }

}





