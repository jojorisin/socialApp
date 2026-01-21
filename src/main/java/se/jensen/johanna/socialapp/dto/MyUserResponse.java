package se.jensen.johanna.socialapp.dto;

public record MyUserResponse(
        String email,
        String username,
        String bio,
        String profileImagePath
) {
}
