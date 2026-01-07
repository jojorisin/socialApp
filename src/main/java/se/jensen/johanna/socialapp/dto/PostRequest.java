package se.jensen.johanna.socialapp.dto;

import jakarta.validation.constraints.NotBlank;

public record PostRequest(
        @NotBlank(message = "Text cant be empty") String text
) {
}
