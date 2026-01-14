package se.jensen.johanna.socialapp.dto.user;

public record UserResponse(
        Long userId,
        String username,
        String role
) {
}
