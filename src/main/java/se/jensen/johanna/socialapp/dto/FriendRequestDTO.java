package se.jensen.johanna.socialapp.dto;

public record FriendRequestDTO(
        Long senderId,
        Long receiverId
) {
}
