package com.example.consultants_api.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "app_users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    private String role;
}