package se.jensen.johanna.socialapp.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.jensen.johanna.socialapp.dto.LoginRequestDTO;
import se.jensen.johanna.socialapp.dto.LoginResponseDTO;
import se.jensen.johanna.socialapp.dto.RegisterUserResponse;
import se.jensen.johanna.socialapp.dto.UserRequest;
import se.jensen.johanna.socialapp.service.TokenService;
import se.jensen.johanna.socialapp.service.UserService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> getToken(@RequestBody
                                                     LoginRequestDTO loginRequestDTO) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.username(),
                        loginRequestDTO.password()
                ));

        String token = tokenService.generateToken(auth);
        return ResponseEntity.ok().body(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@RequestBody
                                                         UserRequest userRequest) {
        RegisterUserResponse userResponse = userService.registerUser(userRequest);
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userRequest.username(),
                        userRequest.password()));
        String token = tokenService.generateToken(auth);
        userResponse.setToken(token);
        return ResponseEntity.ok().body(userResponse);

    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal Jwt jwt) {
        //logout metod, frontend rensar token
        return ResponseEntity.noContent().build();
    }
}
