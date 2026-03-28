package kfu.itis.maslennikov.repository;

import kfu.itis.maslennikov.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String username, String email);
    Optional<User> findByVerificationCode(String verificationCode);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);

    @Query(value = "select u from User u where u.username = :username")
    Optional<User> getByUsername(String username);

    @Query(value = "select * from users u where u.username = ?1", nativeQuery = true)
    Optional<User> getByUsernameNative(String username);
}