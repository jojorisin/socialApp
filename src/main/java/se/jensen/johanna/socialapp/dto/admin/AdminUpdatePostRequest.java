package se.jensen.johanna.socialapp.dto.admin;

import java.time.LocalDateTime;

public record AdminUpdatePostRequest(
        String text,
        LocalDateTime createdAt,
        Long userId
) {
}
