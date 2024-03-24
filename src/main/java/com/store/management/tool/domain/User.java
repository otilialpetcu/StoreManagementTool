package com.store.management.tool.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_store")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    private String password;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "user_role")
    @Enumerated(EnumType.STRING)
    private UserRole userRole = UserRole.CUSTOMER;
}
