package se.jensen.johanna.socialapp.dto.user;

public record UserDTO(
        Long userId,
        String profileImagePath,
        String username,
        String bio
) {
}
