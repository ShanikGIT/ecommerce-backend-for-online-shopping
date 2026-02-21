package com.nikhil.ecommerce_backend.repositories;

import com.nikhil.ecommerce_backend.entities.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
    Optional<User> findByEmailAndIsDeletedFalse(String email);
    boolean existsByEmail(String email);
    @Query("select u.password from User u where u.email=:email")
    String getPassword(@Param("email") String email);
}
