package se.jensen.johanna.socialapp.dto;

import java.time.LocalDateTime;

/**
 * @param postId
 * @param commentId
 * @param userId
 * @param username
 * @param text
 * @param createdAt
 */
public record CommentResponse(
        Long postId,
        Long commentId,
        Long userId,
        String username,
        String text,
        LocalDateTime createdAt
) {
}
