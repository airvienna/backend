package com.airvienna.demo.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class RequestLoginDto {
    @NotNull(message = "Email is required.")
    @Pattern(regexp = "^[A-Za-z0-9._%-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}$", message = "Invalid email format. Please provide a valid email.")
    private String email;

    @NotNull
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()_+,-./:;<=>?@\\[\\]^_`{|}~]).{8,}$",
            message = "Password must be at least 8 characters long and combine letters, numbers, and special symbols.")
    private String password;
}
