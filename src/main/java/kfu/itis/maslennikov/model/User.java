package kfu.itis.maslennikov.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    public User(Long id, String username) {
        this.id = id;
        this.username = username;
    }

    public User() {}

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }


}
