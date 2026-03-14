package kfu.itis.maslennikov.dto;

public class RegisterDto {
    private String username;
    private String password;

    public RegisterDto() {}

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
