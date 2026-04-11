package kfu.itis.maslennikov.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorizeRequests -> authorizeRequests
                .requestMatchers("/index", "/login").permitAll()
                .requestMatchers("/register", "/verification", "/success_sign_up").permitAll()
                .requestMatchers("/hello").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/notes/public").permitAll()
                .requestMatchers("/notes/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/admin/**", "/users/","/users").hasRole("ADMIN")
                .requestMatchers("/error/**").permitAll()
                .anyRequest().authenticated()
        )
                .csrf(csrf -> csrf.ignoringRequestMatchers("/users"))

                .formLogin(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
