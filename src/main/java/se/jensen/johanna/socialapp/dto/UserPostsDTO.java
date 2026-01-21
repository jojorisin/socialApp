package se.jensen.johanna.socialapp.dto;

import java.time.LocalDateTime;

public record UserPostsDTO(
        Long postId,
        String text,
        LocalDateTime createdAt,
        LocalDateTime updatedAt

) {
}
