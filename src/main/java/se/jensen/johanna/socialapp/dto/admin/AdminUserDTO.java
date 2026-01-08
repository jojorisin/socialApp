package se.jensen.johanna.socialapp.dto.admin;

import se.jensen.johanna.socialapp.model.Role;

public record AdminUserDTO(
        Long userId,
        Role role,
        String email,
        String username,
        String bio,
        String profileImagePath
) {
}
