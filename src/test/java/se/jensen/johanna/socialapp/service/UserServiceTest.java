package se.jensen.johanna.socialapp.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import se.jensen.johanna.socialapp.dto.RegisterUserRequest;
import se.jensen.johanna.socialapp.mapper.UserMapper;
import se.jensen.johanna.socialapp.model.Role;
import se.jensen.johanna.socialapp.model.User;
import se.jensen.johanna.socialapp.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private  UserService userService;

    @Test
    @DisplayName("Successfully create user")
    void testCreateUser_Success(){
        //Arrange
        RegisterUserRequest request = new RegisterUserRequest("felicia@gmail.com", "Felicia", "12345678", "12345678");
        String fakeHash = "hashed_password";
        User fakeUser = new User(); // create an empty user to simulate mapping

        when(passwordEncoder.encode(request.password())).thenReturn(fakeHash);
        when(userMapper.toUser(request,fakeHash, Role.MEMBER)).thenReturn(fakeUser);

        //Act
        userService.registerUser(request);

        //Assert
        verify(passwordEncoder, times(1)).encode("12345678");
        verify(userRepository,times(1)).save(fakeUser);

    }

    @Test
    @DisplayName("Fail to create user when email already exists")
    void testRegisterUser_EmailAlreadyExists(){
        //Arrange
        RegisterUserRequest request = new RegisterUserRequest("johanna@gamil.com", "Johanna", "1234", "1234");
        when(userRepository.existsByEmail(request.email())).thenReturn(true);

        //Act & Assert
        assertThrows(RuntimeException.class,() -> {
            userService.registerUser(request);
        });

        //Verify that no user is saved when the email already exists
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Fail to create user when passwords do not match")
    void testCreateUser_PasswordsDoNotMatch(){
        //Arrange
        RegisterUserRequest request = new RegisterUserRequest("Marcel@gamil.com", "Marcel", "123", "WRONG_PASSWORD");

        //Act & Assert
        assertThrows(RuntimeException.class, () -> {
            userService.registerUser(request);
        });

        //Verify that the following interactions never happened
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

}
