package se.jensen.johanna.socialapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @Email @NotBlank String email,
        @NotBlank(message = "Username needs to be unique") String username,
        @NotBlank
        @Size(min = 8, message = "Password needs to be min 8 characters")
        String password,
        @NotBlank(message = "Passwords needs to match")
        String confirmPassword

) {
}
