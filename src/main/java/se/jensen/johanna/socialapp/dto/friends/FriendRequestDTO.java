package se.jensen.johanna.socialapp.dto.friends;

public record FriendRequestDTO(
        Long senderId,
        Long receiverId
) {
}
