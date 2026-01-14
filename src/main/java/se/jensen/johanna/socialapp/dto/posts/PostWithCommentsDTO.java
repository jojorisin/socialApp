package se.jensen.johanna.socialapp.dto.posts;

import se.jensen.johanna.socialapp.dto.comment.CommentDTO;

import java.time.LocalDateTime;
import java.util.List;

public record PostWithCommentsDTO(
        Long postId,
        Long userId,
        String username,
        String text,
        LocalDateTime createdAt,
        List<CommentDTO> comments
) {
}
