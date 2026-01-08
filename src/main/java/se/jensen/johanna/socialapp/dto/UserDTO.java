package se.jensen.johanna.socialapp.dto;

public record UserDTO(
        Long userId,
        String profileImagePath,
        String username,
        String bio
) {
}
