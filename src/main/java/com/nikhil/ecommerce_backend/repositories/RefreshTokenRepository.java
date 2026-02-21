package com.nikhil.ecommerce_backend.repositories;

import com.nikhil.ecommerce_backend.entities.RefreshToken;
import com.nikhil.ecommerce_backend.entities.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long>
{
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);

    @Query("SELECT r FROM RefreshToken r JOIN FETCH r.user WHERE r.token = :token")
    Optional<RefreshToken> findByTokenWithUser(@Param("token") String token);
}
