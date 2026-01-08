package se.jensen.johanna.socialapp.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @param commentId
 * @param userId
 * @param username
 * @param text
 * @param createdAt
 * @param updatedAt
 * @param replies
 */
public record CommentDTO(
        Long commentId,
        Long userId,
        String username,
        String text,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        List<CommentDTO> replies
) {
}
