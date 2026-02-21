package com.nikhil.ecommerce_backend.repositories;

import com.nikhil.ecommerce_backend.entities.ActivationToken;
import com.nikhil.ecommerce_backend.entities.User;
import io.lettuce.core.dynamic.annotation.Param;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ActivationTokenRepository extends JpaRepository<ActivationToken, Long> {

    Optional<ActivationToken> findByToken(String token);

    @Transactional
    @Modifying
    @Query("DELETE FROM ActivationToken t WHERE t.user = :user")
    void deleteByUser(@Param("user") User user);
}
