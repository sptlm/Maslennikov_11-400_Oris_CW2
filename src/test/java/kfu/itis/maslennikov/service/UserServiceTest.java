package kfu.itis.maslennikov.service;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import kfu.itis.maslennikov.config.properties.MailProperties;
import kfu.itis.maslennikov.dto.RegisterDto;
import kfu.itis.maslennikov.dto.UserDto;
import kfu.itis.maslennikov.model.Role;
import kfu.itis.maslennikov.model.User;
import kfu.itis.maslennikov.repository.RoleRepository;
import kfu.itis.maslennikov.repository.UserRepository;
import kfu.itis.maslennikov.service.impl.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JavaMailSender mailSender;

    private MailProperties mailProperties;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        mailProperties = new MailProperties(
                "noreply@test.com",
                "Test Sender",
                "Verify account",
                "Hello $name, open $url",
                "http://localhost:8080"
        );
        userService = new UserService(userRepository, roleRepository, passwordEncoder, mailSender, mailProperties);
    }

    @Test
    void findAllAndFindByIdShouldMapUsers() {
        User user = user(1L, "john", "john@mail.com", "pwd");
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        List<UserDto> all = userService.findAll();
        UserDto byId = userService.findById(1L);

        assertThat(all).hasSize(1);
        assertThat(all.get(0).getUsername()).isEqualTo("john");
        assertThat(byId.getEmail()).isEqualTo("john@mail.com");

        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.findById(2L)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void createShouldValidateUniquenessAndEncodePassword() {
        UserDto dto = new UserDto();
        dto.setUsername("new-user");
        dto.setEmail("new@mail.com");

        when(userRepository.findByUsername("new-user")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("new@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("new-user")).thenReturn("encoded-user");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(100L);
            return u;
        });

        UserDto result = userService.create(dto);

        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getUsername()).isEqualTo("new-user");

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getPassword()).isEqualTo("encoded-user");
    }

    @Test
    void updateShouldHandlePasswordBranchAndDuplicates() {
        User existing = user(5L, "old", "old@mail.com", "old-pwd");
        when(userRepository.findById(5L)).thenReturn(Optional.of(existing));

        UserDto dto = new UserDto();
        dto.setUsername("updated");
        dto.setEmail("updated@mail.com");
        dto.setPassword("new-pass");

        when(userRepository.findByUsername("updated")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("updated@mail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("new-pass")).thenReturn("encoded-pass");
        when(userRepository.save(existing)).thenReturn(existing);

        UserDto result = userService.update(5L, dto);
        assertThat(result.getUsername()).isEqualTo("updated");
        assertThat(existing.getPassword()).isEqualTo("encoded-pass");

        dto.setPassword("   ");
        userService.update(5L, dto);
        verify(passwordEncoder).encode("new-pass");

        User duplicate = user(9L, "updated", "dup@mail.com", "pwd");
        when(userRepository.findByUsername("updated")).thenReturn(Optional.of(duplicate));
        assertThatThrownBy(() -> userService.update(5L, dto)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteShouldDeleteExistingAndThrowForMissing() {
        when(userRepository.existsById(1L)).thenReturn(true);
        userService.delete(1L);
        verify(userRepository).deleteById(1L);

        when(userRepository.existsById(2L)).thenReturn(false);
        assertThatThrownBy(() -> userService.delete(2L)).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void registerVerifyAndSendVerificationMailShouldWork() {
        RegisterDto dto = new RegisterDto();
        dto.setUsername("jane");
        dto.setEmail("jane@mail.com");
        dto.setPassword("password");

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");

        when(userRepository.findByUsername("jane")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("jane@mail.com")).thenReturn(Optional.empty());
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode("password")).thenReturn("enc-password");

        MimeMessage mimeMessage = new MimeMessage(Session.getDefaultInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        userService.register(dto);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getVerificationCode()).isNotBlank();
        assertThat(savedUser.isVerified()).isFalse();
        verify(mailSender).send(mimeMessage);

        User pending = user(3L, "jane", "jane@mail.com", "pwd");
        pending.setVerificationCode("code");
        pending.setVerified(false);
        when(userRepository.findByVerificationCode("code")).thenReturn(Optional.of(pending));

        boolean verified = userService.verify("code");
        assertThat(verified).isTrue();
        assertThat(pending.isVerified()).isTrue();
        assertThat(pending.getVerificationCode()).isNull();

        when(userRepository.findByVerificationCode("absent")).thenReturn(Optional.empty());
        assertThat(userService.verify("absent")).isFalse();

        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.empty());
        when(roleRepository.save(any(Role.class))).thenAnswer(invocation -> invocation.getArgument(0));
        userService.register(dto);
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void sendVerificationMailShouldWrapMailExceptions() {
        RegisterDto dto = new RegisterDto();
        dto.setUsername("bad");
        dto.setEmail("bad@mail.com");

        when(mailSender.createMimeMessage()).thenThrow(new RuntimeException("mail failure"));

        assertThatThrownBy(() -> userService.sendVerificationMail(dto, "code"))
                .isInstanceOf(RuntimeException.class);

        verify(mailSender, never()).send(any(MimeMessage.class));
    }

    private static User user(Long id, String username, String email, String password) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        return user;
    }
}