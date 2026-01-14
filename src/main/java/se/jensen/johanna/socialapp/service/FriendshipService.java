package se.jensen.johanna.socialapp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se.jensen.johanna.socialapp.dto.friends.FriendResponseDTO;
import se.jensen.johanna.socialapp.exception.IllegalFriendshipStateException;
import se.jensen.johanna.socialapp.exception.NotFoundException;
import se.jensen.johanna.socialapp.exception.UnauthorizedAccessException;
import se.jensen.johanna.socialapp.mapper.FriendshipMapper;
import se.jensen.johanna.socialapp.model.Friendship;
import se.jensen.johanna.socialapp.model.FriendshipStatus;
import se.jensen.johanna.socialapp.model.User;
import se.jensen.johanna.socialapp.dto.user.UserListDTO;
import se.jensen.johanna.socialapp.mapper.UserMapper;
import se.jensen.johanna.socialapp.repository.FriendshipRepository;
import se.jensen.johanna.socialapp.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FriendshipService {

    private final FriendshipRepository friendshipRepository;
    private final UserRepository userRepository;
    private final FriendshipMapper friendshipMapper;
    private final UserMapper userMapper;

    /**
     * Creates a new friendship request with status PENDING.
     * Validates that users exist and that no friendship already exists between them.
     */
    public FriendResponseDTO sendFriendRequest(Long senderId, Long receiverId) {
        // Validation: User cannot add themselves
        if (senderId.equals(receiverId)) {
            throw new IllegalArgumentException("You cannot add yourself as a friend.");
        }

        // Validation: Check if friendship already exists in either direction (A->B or B->A)
        if (friendshipRepository.existsBySender_UserIdAndReceiver_UserId(senderId, receiverId) ||
                friendshipRepository.existsBySender_UserIdAndReceiver_UserId(receiverId, senderId)) {
            throw new IllegalStateException("Friendship or request already exists.");
        }

        User sender = userRepository.findById(senderId).orElseThrow(() -> new NotFoundException("Sender with id " + senderId + " not found."));
        User receiver = userRepository.findById(receiverId).orElseThrow(() -> new NotFoundException("Receiver with id " + receiverId + " not found."));

        Friendship friendship = new Friendship();
        friendship.setSender(sender);
        friendship.setReceiver(receiver);
        // Status is PENDING by default

        friendshipRepository.save(friendship); // Saves the new friendship request
        return friendshipMapper.toFriendResponse(friendship);
    }

    /**
     * Accepts an existing friend request.
     * Ensures the user accepting the request is the actual receiver.
     */
    public FriendResponseDTO acceptFriendRequest(
            Long friendshipId,
            Long currentUserId) {

        Friendship friendship = friendshipRepository.findById(friendshipId)
                .orElseThrow(() -> new NotFoundException("Friendship with id " + friendshipId + " not found."));

        // Security check: Only the receiver can accept the request
        if (!friendship.getReceiver().getUserId().equals(currentUserId)) {
            throw new UnauthorizedAccessException("You are not authorized to accept this request.");
        }

        // Validation: Cannot accept a request that has been rejected
        if (friendship.getStatus().equals(FriendshipStatus.REJECTED)) {
            throw new IllegalFriendshipStateException("This request has already been rejected");
        }

        // Validation: Cannot accept a request that is already accepted
        if (friendship.getStatus().equals(FriendshipStatus.ACCEPTED)) {
            throw new IllegalFriendshipStateException("This request has already been accepted.");
        }

        friendship.accept(); // Sets status to ACCEPTED and acceptedAt to now
        friendshipRepository.save(friendship); // Updates the current friendship

        return friendshipMapper.toFriendResponse(friendship);
    }

    /**
     * Rejects a pending friend request.
     * Ensures the user rejecting the request is the actual receiver.
     */
    public FriendResponseDTO rejectFriendRequest(Long friendshipId, Long currentUserId){
        Friendship friendship = friendshipRepository.findById(friendshipId).
                orElseThrow(() -> new NotFoundException("Friendship with id " + friendshipId + " not found."));

        // Security check: Only the receiver can reject the request
        if (!friendship.getReceiver().getUserId().equals(currentUserId)) {
            throw new UnauthorizedAccessException("You are not authorized to reject this request.");
        }

        // Validation: Cannot reject a request that is already accepted
        if (friendship.getStatus().equals(FriendshipStatus.ACCEPTED)) {
            throw new IllegalFriendshipStateException("This request has already been accepted.");
        }
        // Validation: Cannot reject a request that is already rejected
        if (friendship.getStatus().equals(FriendshipStatus.REJECTED)) {
            throw new IllegalFriendshipStateException("This request has already been rejected.");
        }

        friendship.setStatus(FriendshipStatus.REJECTED);
        friendshipRepository.save(friendship);
        return friendshipMapper.toFriendResponse(friendship);
    }

    /**
     * Retrieves all friendships (both pending and accepted) where the user is either sender or receiver.
     */
    public List<Friendship> getFriendships(Long userId) {
        return friendshipRepository.findBySender_UserIdOrReceiver_UserId(userId, userId);
    }

    /**
     * Retrieves only accepted friendships and maps them to a list of Users (the friends).
     */
    public List<UserListDTO> getAcceptedFriendships(Long userId) {
        return friendshipRepository.findFriendshipsByUserIdAndStatus(userId, FriendshipStatus.ACCEPTED)
                .stream()
                .map(friendship -> {
                    // Logic: A friendship consists of a Sender and a Receiver.
                    // We need to find "the other person". If I am the Sender, the friend is the Receiver, and vice versa.
                    User friend = friendship.getSender().getUserId().
                            equals(userId) ? friendship.getReceiver() : friendship.getSender();
                    return userMapper.toUserListDTO(friend);
                })
                .toList();
    }

    /**
     * Retrieves all pending requests involving the user (both sent by them and received by them).
     */
    public List<Friendship> getPendingFriendships(Long userId) {
        return friendshipRepository.findFriendshipsByUserIdAndStatus(userId, FriendshipStatus.PENDING);
    }
}
