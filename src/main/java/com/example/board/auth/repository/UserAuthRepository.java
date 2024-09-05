package com.example.board.auth.repository;

import com.example.board.auth.entity.UserAuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAuthRepository extends JpaRepository<UserAuthEntity, Long> {
    Optional<UserAuthEntity> findByRefreshToken(String token);
    Optional<UserAuthEntity> findFirstByUserIdAndLogout(String userId, Boolean logout);
}
