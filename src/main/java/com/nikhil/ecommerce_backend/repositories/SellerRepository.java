package com.nikhil.ecommerce_backend.repositories;

import com.nikhil.ecommerce_backend.entities.Seller;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface SellerRepository extends JpaRepository<Seller, Long>
{
    boolean existsByGst(String gst);
    boolean existsByCompanyNameIgnoreCase(String companyName);
    boolean existsByCompanyContact(String companyContact);
    Page<Seller> findByEmailContainingIgnoreCase(String email, Pageable pageable);
    Seller findByEmail(String email);
    boolean existsByEmail(String email);
    @Query("select u.password from User u where u.email=:email")
    String getPassword(@Param("email") String email);
}

