package se.jensen.johanna.socialapp.dto;

import se.jensen.johanna.socialapp.model.Role;

public record LoginResponseDTO(
        String accessToken,
        Long userId,
        Role role,
        String username
) {
}
