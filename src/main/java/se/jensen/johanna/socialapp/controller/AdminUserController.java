package se.jensen.johanna.socialapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import se.jensen.johanna.socialapp.dto.admin.AdminUpdateUserRequest;
import se.jensen.johanna.socialapp.dto.admin.AdminUpdateUserResponse;
import se.jensen.johanna.socialapp.dto.admin.AdminUserDTO;
import se.jensen.johanna.socialapp.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<AdminUserDTO>> getAllUsersAdmin() {
        List<AdminUserDTO> userDTOS = userService.findAllUsersAdmin();

        return ResponseEntity.ok(userDTOS);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{userId}")
    public ResponseEntity<AdminUserDTO> getUserAdmin(@PathVariable Long userId) {
        AdminUserDTO userDTO = userService.findUserAdmin(userId);

        return ResponseEntity.ok(userDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);

        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{userId}")
    public ResponseEntity<AdminUpdateUserResponse> updateUserAdmin(
            @PathVariable Long userId,
            @RequestBody AdminUpdateUserRequest userRequest) {
        AdminUpdateUserResponse userResponse = userService.updateUserAdmin(
                userRequest, userId);

        return ResponseEntity.ok(userResponse);
    }
}
