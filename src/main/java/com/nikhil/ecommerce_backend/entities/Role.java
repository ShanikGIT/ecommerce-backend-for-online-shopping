package com.nikhil.ecommerce_backend.entities;

import com.nikhil.ecommerce_backend.constants.RoleAuthority;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "roles")
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity
{
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private RoleAuthority authority;
}
