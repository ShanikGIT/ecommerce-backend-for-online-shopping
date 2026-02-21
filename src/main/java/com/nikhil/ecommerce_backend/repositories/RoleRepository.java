package com.nikhil.ecommerce_backend.repositories;


import com.nikhil.ecommerce_backend.constants.RoleAuthority;
import com.nikhil.ecommerce_backend.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long>
{
    Optional<Object> findByAuthority(RoleAuthority authority);
}
