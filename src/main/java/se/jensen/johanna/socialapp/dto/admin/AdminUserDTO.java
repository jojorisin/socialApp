package se.jensen.johanna.socialapp.dto.admin;

public record AdminUserDTO(
        Long userId,
        String email,
        String username,
        String bio,
        String profileImagePath
) {
}
