package blog.posts.p1.authentication.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
public class SecurityFilter {

    private final AuthenticationProvider authenticationProvider;

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityFilter(AuthenticationProvider authenticationProvider, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.authenticationProvider = authenticationProvider;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrfConfig ->csrfConfig.disable())
                .sessionManagement(sessionMngConfig ->sessionMngConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authConfig ->{
                    authConfig.requestMatchers(HttpMethod.POST, "/auth/register").permitAll();
                    authConfig.requestMatchers(HttpMethod.POST, "/auth/login").permitAll();
                    authConfig.requestMatchers(HttpMethod.POST, "/auth/refresh_token").permitAll();
                    authConfig.requestMatchers(HttpMethod.POST, "/auth/telegram/login").permitAll();
                    authConfig.requestMatchers(HttpMethod.POST, "/auth/telegram/webhook").permitAll();
                    authConfig.requestMatchers("/error").permitAll();
                    authConfig.requestMatchers("/api-docs").permitAll();
                    authConfig.requestMatchers("/swagger-ui/index.html").permitAll();

                    authConfig.requestMatchers(HttpMethod.POST, "/auth/logout").authenticated();
                    authConfig.requestMatchers(HttpMethod.GET, "/products").authenticated();

                    authConfig.anyRequest().denyAll();

                });

        return http.build();

    }
}
