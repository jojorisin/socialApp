package se.jensen.johanna.socialapp.dto;

public record UserResponse(
        Long userId,
        String username,
        String role
) {
}
