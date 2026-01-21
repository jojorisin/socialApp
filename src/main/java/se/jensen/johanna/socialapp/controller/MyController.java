package se.jensen.johanna.socialapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import se.jensen.johanna.socialapp.dto.*;
import se.jensen.johanna.socialapp.service.FriendshipService;
import se.jensen.johanna.socialapp.service.PostService;
import se.jensen.johanna.socialapp.service.UserService;
import se.jensen.johanna.socialapp.util.JwtUtils;

import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/my")
@RequiredArgsConstructor
public class MyController {
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final PostService postService;
    private final JwtUtils jwtUtils;


    @GetMapping
    public ResponseEntity<MyUserResponse> getMe(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwtUtils.extractUserId(jwt);
        MyUserResponse myUserResponse = userService.getAuthenticatedUser(userId);

        return ResponseEntity.ok(myUserResponse);

    }

    @GetMapping("/posts")
    public ResponseEntity<Page<MyPostResponse>> getMyPosts(
            @AuthenticationPrincipal Jwt jwt,
            @ParameterObject @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Long userId = jwtUtils.extractUserId(jwt);
        Page<MyPostResponse> myPosts = postService.findAuthenticatedUserPosts(userId, pageable);
        return ResponseEntity.ok(myPosts);
    }


    /**
     * Returns a list of pending friendrequests for the authenticated user
     * Contains a boolean isIncoming, is true if the user is on the receiving end
     * is false if the user is the sender
     *
     * @param jwt AccessToken containing ID of the authenticated user
     * @return {@link MyFriendRequest}
     */
    @GetMapping("/friend-requests")
    public ResponseEntity<List<MyFriendRequest>> getMyFriendRequests(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwtUtils.extractUserId(jwt);
        List<MyFriendRequest> myFriendRequests = friendshipService.getFriendRequestsForUser(userId);


        return ResponseEntity.ok(myFriendRequests);
    }

    @GetMapping("/friends")
    public ResponseEntity<List<UserListDTO>> getMyFriends(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwtUtils.extractUserId(jwt);

        List<UserListDTO> friends = friendshipService.getFriendsForUser(userId);


        return ResponseEntity.ok(friends);
    }


    @PatchMapping
    public ResponseEntity<UpdateUserResponse> updateMe(@AuthenticationPrincipal Jwt jwt,
                                                       @RequestBody UpdateUserRequest userRequest) {
        Long userId = jwtUtils.extractUserId(jwt);

        UpdateUserResponse userResponse = userService.updateUser(userRequest, userId);
        return ResponseEntity.ok(userResponse);
    }

    @PatchMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        Long userId = jwtUtils.extractUserId(jwt);
        userService.changePassword(userId, changePasswordRequest);

        return ResponseEntity.noContent().build();
    }


    @DeleteMapping
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal Jwt jwt) {
        Long userId = jwtUtils.extractUserId(jwt);

        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();


    }


}
