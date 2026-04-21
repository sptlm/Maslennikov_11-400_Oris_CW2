package kfu.itis.maslennikov.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kfu.itis.maslennikov.aop.Benchmarkable;
import kfu.itis.maslennikov.aop.Metricable;
import kfu.itis.maslennikov.config.properties.MailProperties;
import kfu.itis.maslennikov.dto.RegisterDto;
import kfu.itis.maslennikov.dto.UserDto;
import kfu.itis.maslennikov.mapper.UserMapper;
import kfu.itis.maslennikov.model.Role;
import kfu.itis.maslennikov.model.User;
import kfu.itis.maslennikov.repository.RoleRepository;
import kfu.itis.maslennikov.repository.UserRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
//    // private final UserRepositoryHiber userRepositoryHiber;
//    private final UserRepository userRepository;
//
//    public UserService( // UserRepositoryHiber userRepositoryHiber,
//                       UserRepository userRepository) {
//        // this.userRepositoryHiber = userRepositoryHiber;
//        this.userRepository = userRepository;
//    }

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder, JavaMailSender mailSender, MailProperties mailProperties) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
    }

    @Transactional
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + id));
        return UserMapper.toDto(user);
    }

    @Metricable
    @Benchmarkable
    @Transactional
    public UserDto create(UserDto userDto) {
        validateUniqueness(userDto.getUsername(), userDto.getEmail(), null);
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getUsername()));
        return UserMapper.toDto(userRepository.save(user));
    }



    @Transactional
    public UserDto update(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + id));

        validateUniqueness(userDto.getUsername(), userDto.getEmail(), id);
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());

        // обновляем пароль только если передан новый
        if (userDto.getPassword() != null && !userDto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        return UserMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }

    @Transactional
    public void register(RegisterDto dto) {
        validateUniqueness(dto.getUsername(), dto.getEmail(), null);

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ROLE_USER");
                    return roleRepository.save(role);
                });

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(List.of(userRole));
        String verificationCode = UUID.randomUUID().toString();
        user.setVerificationCode(verificationCode);
        user.setVerified(false);
        sendVerificationMail(dto, verificationCode);
        userRepository.save(user);
    }

    @Transactional
    public boolean verify(String verificationCode) {
        Optional<User> userOptional = userRepository.findByVerificationCode(verificationCode);
        if (userOptional.isEmpty()) {
            return false;
        }
        User user = userOptional.get();
        user.setVerificationCode(null);
        user.setVerified(true);
        userRepository.save(user);
        return true;
    }

    private void validateUniqueness(String username, String email, Long excludedUserId) {
        userRepository.findByUsername(username)
                .filter(user -> !user.getId().equals(excludedUserId))
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Имя пользователя уже занято: " + username);
                });

        userRepository.findByEmail(email)
                .filter(user -> !user.getId().equals(excludedUserId))
                .ifPresent(user -> {
                    throw new IllegalArgumentException("Почта уже занята: " + email);
                });
    }

    public void sendVerificationMail(RegisterDto registerDto, String verificationCode) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        String content = mailProperties.content();
        try {
            mimeMessageHelper.setFrom(mailProperties.from(), mailProperties.sender());
            mimeMessageHelper.setTo(registerDto.getEmail());
            mimeMessageHelper.setSubject(mailProperties.subject());

            content = content.replace("$name", registerDto.getUsername());
            content = content.replace("$url", mailProperties.baseUrl() +
                    "/verification?code=" + verificationCode);

            mimeMessageHelper.setText(content, true);

            mailSender.send(mimeMessage);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
