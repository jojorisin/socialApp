package se.jensen.johanna.socialapp.dto;

public record LoginResponseDTO(
        String accessToken,
        String refreshToken,
        Long userId
) {
}
