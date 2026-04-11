package kfu.itis.maslennikov.service;

import kfu.itis.maslennikov.model.User;
import kfu.itis.maslennikov.repository.UserRepository;
import kfu.itis.maslennikov.service.security.CustomUserDetails;
import kfu.itis.maslennikov.service.security.CustomUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsernameShouldReturnCustomUserDetails() {
        User user = new User();
        user.setUsername("john");
        user.setPassword("pwd");
        user.setVerified(false);

        when(userRepository.findByUsernameOrEmail("john", "john")).thenReturn(Optional.of(user));

        CustomUserDetails loaded = (CustomUserDetails) customUserDetailsService.loadUserByUsername("john");
        assertThat(loaded.getUsername()).isEqualTo("john");
    }

    @Test
    void loadUserByUsernameShouldThrowWhenUserNotFound() {
        when(userRepository.findByUsernameOrEmail("missing", "missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("missing"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}