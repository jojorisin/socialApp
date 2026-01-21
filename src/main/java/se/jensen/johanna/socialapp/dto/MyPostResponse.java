package se.jensen.johanna.socialapp.dto;

import java.time.LocalDateTime;

public record MyPostResponse(
        Long postId,
        String text,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
