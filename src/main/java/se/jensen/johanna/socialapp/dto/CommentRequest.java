package se.jensen.johanna.socialapp.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * @param text
 */
public record CommentRequest(
        @NotBlank(message = "Comment can't be empty.")
        String text
) {
}
