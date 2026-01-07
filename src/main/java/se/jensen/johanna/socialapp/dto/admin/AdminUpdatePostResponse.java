package se.jensen.johanna.socialapp.dto.admin;

import java.time.LocalDateTime;

public record AdminUpdatePostResponse(
        String text,
        LocalDateTime createdAt,
        Long userId,
        Long postId
) {
}
