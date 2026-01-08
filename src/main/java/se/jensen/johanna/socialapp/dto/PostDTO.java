package se.jensen.johanna.socialapp.dto;

import java.time.LocalDateTime;
import java.util.List;

public record PostDTO(
        Long postId,
        Long userId,
        String username,
        String text,
        LocalDateTime createdAt,
        List<CommentDTO> comments
) {
}
