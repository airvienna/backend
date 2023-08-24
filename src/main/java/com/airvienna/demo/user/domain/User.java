package com.airvienna.demo.user.domain;

import com.airvienna.demo.common.domain.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Getter
@EqualsAndHashCode
@Table(name = "users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Last name cannot be null.")
    @Size(min = 1, max = 30, message = "Last name must be between 1 to 30 characters.")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotNull(message = "First name cannot be null.")
    @Size(min = 1, max = 40, message = "First name must be between 1 to 40 characters.")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotNull(message = "Birth date cannot be null.")
    @Past(message = "Birth date must be in the past.")
    @Column(nullable = false)
    private LocalDate birth;

    @NotNull(message = "Email cannot be null.")
    @Pattern(regexp = "^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$", message = "Email format is invalid. Please enter a valid email.")
    @Column(nullable = false, unique = true)
    private String email;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()_+,-./:;<=>?@\\[\\]^_`{|}~]).{8,}$",
            message = "Password must be at least 8 characters long and combine letters, numbers, and special symbols.")
    private String password;

    @Pattern(regexp = "^[+]?[0-9]{1,15}$", message = "Phone number format is invalid. Please enter a valid phone number.")
    @Column(unique = true)
    private String phone;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Builder
    public User(String lastName, String firstName, LocalDate birth, String email, String password, String phone, String profileImageUrl) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.birth = birth;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.profileImageUrl = profileImageUrl;
    }
}