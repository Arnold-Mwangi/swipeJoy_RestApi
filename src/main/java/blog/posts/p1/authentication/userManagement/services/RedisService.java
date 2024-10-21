package blog.posts.p1.authentication.userManagement.services;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

    @Service
    public class RedisService {

        private final RedisTemplate<String, String> redisTemplate;

        public RedisService(RedisTemplate<String, String> redisTemplate) {
            this.redisTemplate = redisTemplate;
        }

        public void addToBlacklist(String token, Instant expiration) {
            Duration ttl = Duration.between(Instant.now(), expiration);
            redisTemplate.opsForValue().set(token, "blacklisted", ttl);
        }

        public boolean isBlacklisted(String token) {
            return redisTemplate.hasKey(token);
        }
    }


