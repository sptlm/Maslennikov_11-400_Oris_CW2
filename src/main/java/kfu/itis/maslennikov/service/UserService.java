package kfu.itis.maslennikov.service;

import kfu.itis.maslennikov.dto.UserDto;
import kfu.itis.maslennikov.mapper.UserMapper;
import kfu.itis.maslennikov.model.User;
import kfu.itis.maslennikov.repository.UserRepository;
import kfu.itis.maslennikov.repository.UserRepositoryHiber;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class UserService {
    // private final UserRepositoryHiber userRepositoryHiber;
    private final UserRepository userRepository;

    public UserService( // UserRepositoryHiber userRepositoryHiber,
                       UserRepository userRepository) {
        // this.userRepositoryHiber = userRepositoryHiber;
        this.userRepository = userRepository;
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
        return UserMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public UserDto update(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found: " + id));

        user.setUsername(userDto.getUsername());
        return UserMapper.toDto(userRepository.save(user));
    }

    @Transactional
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NoSuchElementException("User not found: " + id);
        }
        userRepository.deleteById(id);
    }
}
