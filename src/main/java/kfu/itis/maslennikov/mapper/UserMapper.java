package kfu.itis.maslennikov.mapper;

import kfu.itis.maslennikov.dto.UserDto;
import kfu.itis.maslennikov.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public static UserDto toDto(User user) {
        return new UserDto(user.getId(), user.getUsername());
    }

    public static User toEntity(UserDto dto) {
        return new User(dto.getId(), dto.getUsername());
    }
}
