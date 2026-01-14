package se.jensen.johanna.socialapp.dto;

import se.jensen.johanna.socialapp.dto.posts.PostWithCommentsDTO;

import java.util.List;

public record HomePageResponse(
        String message,
        List<PostWithCommentsDTO> posts
) {
}
