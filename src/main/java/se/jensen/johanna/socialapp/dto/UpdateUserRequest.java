package se.jensen.johanna.socialapp.dto;

public record UpdateUserRequest(
        String username,
        String bio,
        String profileImagePath
) {
}
