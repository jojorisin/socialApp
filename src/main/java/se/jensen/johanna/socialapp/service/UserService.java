package se.jensen.johanna.socialapp.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import se.jensen.johanna.socialapp.dto.*;
import se.jensen.johanna.socialapp.dto.admin.*;
import se.jensen.johanna.socialapp.exception.NotFoundException;
import se.jensen.johanna.socialapp.exception.NotUniqueException;
import se.jensen.johanna.socialapp.exception.PasswordMisMatchException;
import se.jensen.johanna.socialapp.mapper.UserMapper;
import se.jensen.johanna.socialapp.model.Role;
import se.jensen.johanna.socialapp.model.User;
import se.jensen.johanna.socialapp.repository.UserRepository;

import java.util.List;

/**
 * Service class responsible for managing user-related operations.
 * This includes user registration, profile management, administrative updates,
 * and retrieval of user-specific data such as profiles and friend lists.
 */

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final FriendshipService friendshipService;
    private final PostService postService;


    /* *********************PUBLIC******************* */

    /**
     * Registers a new user in the system.
     * Validates credentials, hashes the password, and assigns the default MEMBER role.
     *
     * @param registerUserRequest the request object containing registration details
     * @throws PasswordMisMatchException if the password and confirmation-password do not match
     * @throws NotUniqueException        if the email or username is already in use
     */
    public void registerUser(RegisterUserRequest registerUserRequest) {
        log.info("Trying to register new user with email={}", registerUserRequest.email());
        validateCredentials(registerUserRequest);

        String hashedPw = passwordEncoder.encode(registerUserRequest.password());
        User user = userMapper.toUser(registerUserRequest, hashedPw, Role.MEMBER);
        userRepository.save(user);

        log.info("New user registered with id={} and email={}", user.getUserId(), user.getEmail());

    }


    /**
     * Retrieves a list of all users with the MEMBER role.
     *
     * @return a list of {@link UserListDTO} With less detailed information for a list
     */
    public List<UserListDTO> findAllUsers() {
        return userRepository.findAllUsersByRole(Role.MEMBER).stream()
                .map(userMapper::toUserListDTO).toList();

    }


    /**
     * Finds a specific user by their ID and returns public user data.
     *
     * @param userId the ID of the user to find
     * @return the user details as a {@link UserDTO}
     * @throws NotFoundException if the user does not exist
     */
    public UserDTO findUser(Long userId) {
        return userRepository.findById(userId)
                .map(userMapper::toUserDTO).orElseThrow(NotFoundException::new);
    }


    /* *********************AUTHENTICATED USER********************* */

    /**
     * Retrieves the authenticated User with detailed information
     *
     * @param userId ID of authenticated user
     * @return {@link MyUserResponse} A detailed user-information response
     */
    public MyUserResponse getAuthenticatedUser(Long userId) {
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found."));

        return userMapper.toMyUserResponse(currentUser);
    }


    /**
     * Updates the password for an authenticated user.
     * This method validates that the current password matches the one stored in the database,
     * and that the new password and its confirmation are identical. If valid, the new
     * password is encrypted and saved.
     *
     * @param userId          the ID of the user whose password is to be changed
     * @param passwordRequest the request object containing the current password,
     *                        the new password, and the confirmation of the new password
     * @throws NotFoundException         if no user is found with the provided ID
     * @throws PasswordMisMatchException if the current password is incorrect or,
     *                                   if the new password and confirmation do not match
     */
    public void changePassword(Long userId, ChangePasswordRequest passwordRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User does not exist in database"));
        if (!passwordEncoder.matches(passwordRequest.currentPassword(), user.getPassword())) {
            log.warn("Password mismatch for user with id={}", userId);
            throw new PasswordMisMatchException("Password does not match current password");
        }
        if (!passwordRequest.newPassword().equals(passwordRequest.confirmNewPassword())) {
            log.warn("Password mismatch for user with id={}", userId);
            throw new PasswordMisMatchException("New passwords do not match");
        }
        String hashedPw = passwordEncoder.encode(passwordRequest.newPassword());
        user.setPassword(hashedPw);
        log.info("Password changed for user with id={}", userId);
        userRepository.save(user);

    }


    /**
     * Updates user information for authenticated user
     *
     * @param userRequest the request object containing updated user details
     * @param userId      the ID of the user to update
     * @return the updated user details as an {@link UpdateUserResponse}
     * @throws NotFoundException if the user with the given ID does not exist
     */
    public UpdateUserResponse updateUser(UpdateUserRequest userRequest, Long userId) {
        log.info("Trying to update user with id={}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("Could not update user - user with id={} not found", userId);
                    return new NotFoundException();
                });
        userMapper.updateUser(userRequest, user);
        userRepository.save(user);

        log.info("User with id={} updated", userId);
        return userMapper.toUpdateUserResponse(user);


    }


    /**
     * Finds a specific user by their ID and returns detailed administrative data.
     *
     * @param userId the ID of the user to find
     * @return the user details as an {@link AdminUserDTO}
     * @throws NotFoundException if the user does not exist
     */
    public AdminUserDTO findUserAdmin(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);

        return userMapper.toAdminUserDTO(user);
    }


    /**
     * Deletes a user from the system by their ID.
     *
     * @param userId the ID of the user to delete
     * @throws NotFoundException if the user does not exist
     */
    public void deleteUser(Long userId) {
        log.info("Trying to delete user with id={}", userId);
        User userToDelete = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Could not remove - user with id={} was not found", userId);
            return new NotFoundException();
        });
        userRepository.delete(userToDelete);
        log.info("User with id={} removed", userId);
    }



    /* *************************ADMIN METHODS ************************** */


    /**
     * Retrieves a list of all users in the system for administrative purposes.
     *
     * @return a list of {@link AdminUserDTO}
     */
    public List<AdminUserDTO> findAllUsersAdmin() {
        return userRepository.findAll().stream()
                .map(userMapper::toAdminUserDTO).toList();
    }


    /**
     * Updates and saves the Role for a user. Intended for Admin use.
     *
     * @param request {@link RoleRequest} contains the email of the user and the new role type
     * @return {@link RoleResponse} containing the updated role information
     * @throws NotFoundException if the user with the specified email is not found
     */
    public RoleResponse addRole(RoleRequest request) {
        User user = userRepository.findByEmail(request.email()).orElseThrow(() -> {
            log.warn("Could not update role - user with email={} not found", request.email());
            return new NotFoundException();
        });
        log.info("Admin role-update initiated for user with email={}", user.getEmail());
        user.setRole(request.role());
        userRepository.save(user);
        return new RoleResponse(user.getEmail(), user.getRole());
    }


    /**
     * Performs an administrative update of a user's details.
     *
     * @param userRequest the request object containing updated user details for admin
     * @param userId      the ID of the user to update
     * @return the updated user data as an {@link AdminUpdateUserResponse}
     * @throws NotFoundException if the user does not exist
     */
    public AdminUpdateUserResponse updateUserAdmin(AdminUpdateUserRequest userRequest,
                                                   Long userId) {
        log.info("Admin update initiated for user with id={}", userId);
        User user = userRepository.findById(userId).orElseThrow(() -> {
            log.warn("Could not update User - User with id={} not found", userId);
            return new NotFoundException();
        });
        userMapper.updateUserAdmin(userRequest, user);
        userRepository.save(user);

        return userMapper.toAdminResponse(user);

    }

    /**
     * Validates that the registration details are unique and consistent.
     * Checks if passwords match and if the email/username is already registered.
     *
     * @param registerUserRequest the request object to validate
     * @throws PasswordMisMatchException if passwords do not match
     * @throws NotUniqueException        if email or username is already taken
     */

    public void validateCredentials(RegisterUserRequest registerUserRequest) {
        if (!registerUserRequest.password().equals(registerUserRequest.confirmPassword())) {
            log.warn("Password mismatch during registration for email={}", registerUserRequest.email());
            throw new PasswordMisMatchException();
        }
        if (userRepository.existsByEmail(registerUserRequest.email())) {
            log.warn("Registration attempt with already registered email={}", registerUserRequest.email());
            throw new NotUniqueException("Email is already registered. Log in or try different email.");
        }
        if (userRepository.existsByUsername(registerUserRequest.username())) {
            log.warn("Registration attempt with already taken username={}", registerUserRequest.username());
            throw new NotUniqueException("Username is already registered. Please choose a unique username.");
        }
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User does not exist in database"));
    }


}
