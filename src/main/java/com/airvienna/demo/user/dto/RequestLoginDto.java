package com.airvienna.demo.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RequestLoginDto {
    @NotNull(message = "Email is required.")
    @Pattern(regexp = "^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$", message = "Invalid email format. Please provide a valid email.")
    private String email;

    @NotNull
    @Pattern(regexp = "^(?:(?=.*[a-zA-Z])(?=.*[\\W_])|(?=.*[a-zA-Z])(?=.*\\d)|(?=.*\\d)(?=.*[\\W_])).{8,}$",
            message = "Password must be at least 8 characters long and combine at least two of the following: letters, numbers, and special symbols.")
    private String password;
}
