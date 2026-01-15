package se.jensen.johanna.socialapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import se.jensen.johanna.socialapp.dto.*;
import se.jensen.johanna.socialapp.dto.user.UserDTO;
import se.jensen.johanna.socialapp.dto.user.UserListDTO;
import se.jensen.johanna.socialapp.dto.user.UserProfileDTO;
import se.jensen.johanna.socialapp.dto.user.UserWithPostsDTO;
import se.jensen.johanna.socialapp.model.Friendship;
import se.jensen.johanna.socialapp.model.User;
import se.jensen.johanna.socialapp.service.FriendshipService;
import se.jensen.johanna.socialapp.service.UserService;
import se.jensen.johanna.socialapp.util.JwtUtils;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final FriendshipService friendshipService;

    /**
     * Retrieves a list of all users with the role MEMBER.
     * Returns a simplified DTO (UserListDTO) containing only essential info like ID and username.
     * This is typically used for searching or listing users to add.
     */
    @GetMapping
    public ResponseEntity<List<UserListDTO>> getAllUsers() {
        List<UserListDTO> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Retrieves specific details for a single user.
     */
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userId) {
        UserDTO userDTO = userService.findUser(userId);
        return ResponseEntity.ok(userDTO);

    }

    /**
     * Retrieves a complete user profile.
     * This aggregates data from multiple services:
     * 1. User details and their posts (from UserService).
     * 2. A list of accepted friends (from FriendshipService).
     */
    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileDTO> getUserProfile(
            @PathVariable Long userId
    ){
        // 1. Fetch user details and their posts
        UserWithPostsDTO user = userService.getUserWithPosts(userId);
        
        // 2. Fetch the list of friends (users who have an ACCEPTED friendship with this user)
        List<UserListDTO> friends = friendshipService.getAcceptedFriendships(userId);

        // TODO: GET FRIEND REQUEST WITH PENDING STATE

        // 3. Combine them into a single Profile DTO
        UserProfileDTO userProfile = new UserProfileDTO(friends,user);

        return ResponseEntity.ok(userProfile);




    }


    /**
     * Updates the currently logged-in user's information.
     * Uses @AuthenticationPrincipal to securely get the ID from the JWT token, preventing users from updating others.
     */
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/me")
    public ResponseEntity<UpdateUserResponse> updateUser(@AuthenticationPrincipal Jwt jwt,
                                                         @RequestBody UpdateUserRequest userRequest) {
        Long userId = jwtUtils.extractUserId(jwt);

        UpdateUserResponse userResponse = userService.updateUser(userRequest, userId);
        return ResponseEntity.ok(userResponse);
    }


    /**
     * Deletes the currently logged-in user's account.
     */
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal
                                         Jwt jwt) {
        Long userId = jwtUtils.extractUserId(jwt);

        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();


    }


}
