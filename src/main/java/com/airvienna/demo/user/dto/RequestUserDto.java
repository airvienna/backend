package com.airvienna.demo.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@Getter
@Setter
public class RequestUserDto {
    @NotNull(message = "Last name is required.")
    @Size(min = 1, max = 30, message = "Last name must be between 1 to 30 characters.")
    private String lastName;

    @NotNull(message = "First name is required.")
    @Size(min = 1, max = 40, message = "First name must be between 1 to 40 characters.")
    private String firstName;

    @NotNull(message = "Birth date is required.")
    @Past(message = "Birth date must be in the past.")
    private LocalDate birth;

    @NotNull(message = "Email is required.")
    @Pattern(regexp = "^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$", message = "Invalid email format. Please provide a valid email.")
    private String email;

    @Pattern(regexp = "^(?=(?:[^a-zA-Z]*[a-zA-Z]))(?=(?:\\D*\\d))(?=(?:[^\\W_]*[\\W_])).{8,}$",
            message = "Password must be at least 8 characters long and combine at least two of the following: letters, numbers, and special symbols.")
    private String password;

    @Pattern(regexp = "^[+]?[0-9]{1,15}$", message = "Invalid phone number. Please provide a valid phone number.")
    private String phone;
}
