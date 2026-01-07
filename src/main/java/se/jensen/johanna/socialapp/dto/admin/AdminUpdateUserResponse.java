package se.jensen.johanna.socialapp.dto.admin;

import se.jensen.johanna.socialapp.model.Role;

public record AdminUpdateUserResponse(
        String email,
        String username,
        Role role,
        String bio,
        String profileImagePath
) {
}
