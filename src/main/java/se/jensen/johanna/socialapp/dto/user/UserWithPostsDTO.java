package se.jensen.johanna.socialapp.dto.user;

import se.jensen.johanna.socialapp.dto.posts.PostResponseDTO;

import java.util.List;

public record UserWithPostsDTO(
        String username,
        Long userId,
        List<PostResponseDTO> posts
) {
}
