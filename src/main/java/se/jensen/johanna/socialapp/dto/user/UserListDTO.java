package se.jensen.johanna.socialapp.dto.user;

public record UserListDTO(
        Long userId,
        String username,
        String profileImagePath
) {
}
