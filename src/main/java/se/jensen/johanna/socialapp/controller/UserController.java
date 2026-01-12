package se.jensen.johanna.socialapp.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import se.jensen.johanna.socialapp.dto.*;
import se.jensen.johanna.socialapp.dto.admin.AdminUpdateUserRequest;
import se.jensen.johanna.socialapp.dto.admin.AdminUpdateUserResponse;
import se.jensen.johanna.socialapp.dto.admin.AdminUserDTO;
import se.jensen.johanna.socialapp.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    //OBS vilka är för admin vilka för user


    /*@PostMapping("/register")
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserRequest userRequest) {
        UserResponse userResponse = userService.registerUser(userRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);

    }*/

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<List<AdminUserDTO>> getAllUsersAdmin() {
        List<AdminUserDTO> userDTOS = userService.findAllUsersAdmin();

        return ResponseEntity.ok(userDTOS);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/{userId}")
    public ResponseEntity<AdminUserDTO> getUserAdmin(@PathVariable Long userId) {
        AdminUserDTO userDTO = userService.findUserAdmin(userId);

        return ResponseEntity.ok(userDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/register")
    public ResponseEntity<UserResponse> createAdminUser(@RequestBody @Valid UserRequest userRequest) {

        UserResponse userResponse = userService.registerAdminUser(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/admin/{userId}")
    public ResponseEntity<AdminUpdateUserResponse> updateUserAdmin(
            @PathVariable Long userId,
            @RequestBody AdminUpdateUserRequest userRequest) {
        AdminUpdateUserResponse userResponse = userService.updateUserAdmin(
                userRequest, userId);

        return ResponseEntity.ok(userResponse);
    }

    //OBS hämta inte ADMIN för users att se
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }


    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Long userId) {
        UserDTO userDTO = userService.findUser(userId);
        return ResponseEntity.ok(userDTO);

    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/me")
    //kan endast nå av authenticated
    public ResponseEntity<UpdateUserResponse> updateUser(@AuthenticationPrincipal Jwt jwt,
                                                         @RequestBody UpdateUserRequest userRequest) {

        UpdateUserResponse userResponse = userService.updateUser(userRequest, jwt.getClaim("userId"));
        return ResponseEntity.ok(userResponse);
    }


    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteMe(@AuthenticationPrincipal
                                         Jwt jwt) {

        userService.deleteUser(jwt.getClaim("userId"));

        return ResponseEntity.noContent().build();


    }

}
