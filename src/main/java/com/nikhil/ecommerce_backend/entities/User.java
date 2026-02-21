package com.nikhil.ecommerce_backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity
{
    @NotBlank
    @Size(min = 2, max = 50)
    private String firstName;

    @Size(max = 50)
    private String middleName;

    @NotBlank
    @Size(min = 2, max = 50)
    private String lastName;

    @NotBlank
    @Email
    @Column(nullable = false)
    private String email;

    @NotBlank
    private String password;
    private boolean isDeleted;
    private boolean isActive;
    private boolean isExpired;
    private boolean isLocked;
    private Date passwordUpdateDate;

    private int invalid_attempt_count;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}
