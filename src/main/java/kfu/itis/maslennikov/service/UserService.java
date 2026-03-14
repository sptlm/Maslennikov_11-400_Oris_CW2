package kfu.itis.maslennikov.service;

import kfu.itis.maslennikov.dto.RegisterDto;
import kfu.itis.maslennikov.dto.UserDto;
import kfu.itis.maslennikov.mapper.UserMapper;
import kfu.itis.maslennikov.model.Role;
import kfu.itis.maslennikov.model.User;
import kfu.itis.maslennikov.repository.RoleRepository;
import kfu.itis.maslennikov.repository.UserRepository;
import kfu.itis.maslennikov.repository.UserRepositoryHiber;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
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
    private final RoleRepository roleRepository;       // +
    private final PasswordEncoder passwordEncoder;     // +

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
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

    @Transactional
    public UserDto create(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setPassword(passwordEncoder.encode(userDto.getUsername()));
        return UserMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public UserDto update(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + id));

        user.setUsername(userDto.getUsername());

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
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Пользователь уже существует: " + dto.getUsername());
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setName("ROLE_USER");
                    return roleRepository.save(role);
                });

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRoles(List.of(userRole));
        userRepository.save(user);
    }
}
