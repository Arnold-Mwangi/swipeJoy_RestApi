package blog.posts.p1.authentication.userManagement.services;

import blog.posts.p1.authentication.userManagement.dto.Request.TelegramAuthRequest;
import blog.posts.p1.authentication.userManagement.entity.User;
import blog.posts.p1.authentication.userManagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
public class TelegramAuthService {

    @Autowired
    private UserRepository userRepository;

    @Value("${telegram.bot.token}")
    private String botToken;

    public boolean verifyTelegramAuth(TelegramAuthRequest request) {
        String dataCheckString = buildDataCheckString(request);
        String secretKey = generateSecretKey();
        String hash = generateHash(dataCheckString, secretKey);
        return hash.equals(request.getHash());
    }

    public User createOrUpdateUser(TelegramAuthRequest request) {
        Optional<User> existingUser = userRepository.findByTelegramId(request.getId());
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            updateUserDetails(user, request);
            return userRepository.save(user);
        } else {
            User newUser = createNewUser(request);
            return userRepository.save(newUser);
        }
    }

    private String buildDataCheckString(TelegramAuthRequest request) {
        return String.format("auth_date=%s\nfirst_name=%s\nid=%s\nusername=%s",
                request.getAuthDate(), request.getFirstName(), request.getId(), request.getUsername());
    }

    private String generateSecretKey() {
        try {
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(botToken.getBytes(), "HmacSHA256");
            sha256HMAC.init(secretKey);
            return bytesToHex(sha256HMAC.doFinal("WebAppData".getBytes()));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error generating secret key", e);
        }
    }

    private String generateHash(String data, String key) {
        try {
            Mac sha256HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            sha256HMAC.init(secretKey);
            return bytesToHex(sha256HMAC.doFinal(data.getBytes()));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    private void updateUserDetails(User user, TelegramAuthRequest request) {
        user.setTelegramUsername(request.getUsername());
    }


    private User createNewUser(TelegramAuthRequest request) {
        User newUser = new User();
        newUser.setTelegramId(request.getId());
        newUser.setTelegramUsername(request.getUsername());
        return newUser;
    }
}
