package com.nikhil.ecommerce_backend.repositories;

import com.nikhil.ecommerce_backend.entities.Customer;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>
{
    Optional<Customer> findByEmail(String email);
    Page<Customer> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    @Query("select u.password from User u where u.email=:email")
    String getPassword(@Param("email") String email);

}

