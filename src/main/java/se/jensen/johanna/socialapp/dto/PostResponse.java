package se.jensen.johanna.socialapp.dto;

import java.time.LocalDateTime;

public record PostResponse(
        Long postId,
        String text,
        LocalDateTime createdAt
) {
}
