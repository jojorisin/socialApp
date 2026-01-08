package se.jensen.johanna.socialapp.dto;

import java.time.LocalDateTime;

/**
 * @param parentId
 * @param commentId
 * @param userId
 * @param username
 * @param text
 * @param createdAt
 */
public record ReplyCommentResponse(
        Long parentId,
        Long commentId,
        Long userId,
        String username,
        String text,
        LocalDateTime createdAt
) {
}
