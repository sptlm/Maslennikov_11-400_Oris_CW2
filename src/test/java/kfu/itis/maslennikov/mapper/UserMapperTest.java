package kfu.itis.maslennikov.mapper;

import kfu.itis.maslennikov.dto.UserDto;
import kfu.itis.maslennikov.model.User;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserMapperTest {

    @Test
    void toDtoShouldMapEntityFields() {
        User user = new User(10L, "neo", "neo@mail.com");

        UserDto dto = UserMapper.toDto(user);

        assertThat(dto.getId()).isEqualTo(10L);
        assertThat(dto.getUsername()).isEqualTo("neo");
        assertThat(dto.getEmail()).isEqualTo("neo@mail.com");
    }

    @Test
    void toEntityShouldMapDtoFields() {
        UserDto dto = new UserDto(7L, "trinity", "tri@mail.com");

        User user = UserMapper.toEntity(dto);

        assertThat(user.getId()).isEqualTo(7L);
        assertThat(user.getUsername()).isEqualTo("trinity");
        assertThat(user.getEmail()).isEqualTo("tri@mail.com");
    }
}