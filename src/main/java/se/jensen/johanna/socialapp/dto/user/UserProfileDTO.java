package se.jensen.johanna.socialapp.dto.user;

import java.util.List;

public record UserProfileDTO(
        List<UserListDTO> friends,
        UserWithPostsDTO userAndPosts
) {
}
