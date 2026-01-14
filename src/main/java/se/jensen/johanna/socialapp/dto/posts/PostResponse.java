package se.jensen.johanna.socialapp.dto.posts;

import java.time.LocalDateTime;

public record PostResponse(
        Long postId,
        String text,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
