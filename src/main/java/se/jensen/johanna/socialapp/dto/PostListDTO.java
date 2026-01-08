package se.jensen.johanna.socialapp.dto;

import java.time.LocalDateTime;

public record PostListDTO(
        Long postId,
        Long userId,
        String username,
        String text,
        LocalDateTime createdAt
) {
}
