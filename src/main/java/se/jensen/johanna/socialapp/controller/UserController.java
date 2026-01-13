package se.jensen.johanna.socialapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import se.jensen.johanna.socialapp.dto.*;
import se.jensen.johanna.socialapp.service.UserService;
import se.jensen.johanna.socialapp.util.JwtUtils;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtUtils jwtUtils;

    //OBS vilka är för admin vilka för user


    //Hämtar alla users med role MEMBER.
    //Userlist innehåller mindre info
    @GetMapping
    public ResponseEntity<List<UserListDTO>> getAllUsers() {
        List<UserListDTO> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    //Visar användarprofil
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userId) {
        UserDTO userDTO = userService.findUser(userId);
        return ResponseEntity.ok(userDTO);

    }

    //Visar användarprofil med alla posts den gjort
    @GetMapping("{userId}/with-posts")
    public ResponseEntity<UserWithPostsDTO> getUserWithPosts(@PathVariable Long userId) {

        UserWithPostsDTO userWithPostsDTO = userService.getUserWithPosts(userId);
        return ResponseEntity.ok(userWithPostsDTO);
    }


    //me används för att man inte ska kunna skriva i vilket id som helst.
    //Är den inloggade användaren
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/me")
    public ResponseEntity<UpdateUserResponse> updateUser(@AuthenticationPrincipal Jwt jwt,
                                                         @RequestBody UpdateUserRequest userRequest) {
        Long userId = jwtUtils.extractUserId(jwt);

        UpdateUserResponse userResponse = userService.updateUser(userRequest, userId);
        return ResponseEntity.ok(userResponse);
    }


    //Inloggade användaren
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal
                                         Jwt jwt) {
        Long userId = jwtUtils.extractUserId(jwt);

        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();


    }


}
