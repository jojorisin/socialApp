package se.jensen.johanna.socialapp.dto;

public record UpdateUserResponse(
        String username,
        String bio,
        String profileImagePath
) {
}
