package com.example.consultants_api.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(name = "consultants")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Consultant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @Email
    @NotBlank
    @Column(unique = true)
    private String email;

    private String phone;
    private String specialty;
    private String company;
}