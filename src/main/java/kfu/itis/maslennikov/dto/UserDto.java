package kfu.itis.maslennikov.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDto {
    private Long id;
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)  // ← в ответе не отдаётся
    private String password;

    public UserDto() {}

    public UserDto(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}