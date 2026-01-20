package se.jensen.johanna.socialapp.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import se.jensen.johanna.socialapp.dto.LoginRequestDTO;
import se.jensen.johanna.socialapp.dto.LoginResponseDTO;
import se.jensen.johanna.socialapp.dto.RefreshTokenResponse;
import se.jensen.johanna.socialapp.dto.RegisterUserRequest;
import se.jensen.johanna.socialapp.exception.RefreshTokenException;
import se.jensen.johanna.socialapp.model.RefreshToken;
import se.jensen.johanna.socialapp.security.MyUserDetails;
import se.jensen.johanna.socialapp.service.RefreshTokenService;
import se.jensen.johanna.socialapp.service.TokenService;
import se.jensen.johanna.socialapp.service.UserService;
import se.jensen.johanna.socialapp.util.CookieUtils;


@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final CookieUtils cookieUtils;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> getToken(@RequestBody
                                                     LoginRequestDTO loginRequestDTO,
                                                     HttpServletResponse httpServletResponse) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequestDTO.username(),
                        loginRequestDTO.password()
                ));


        LoginResponseDTO loginResponseDTO = createLoginResponse(auth);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(loginResponseDTO.userId());
        ResponseCookie responseCookie = cookieUtils.createRefreshCookie(refreshToken.getToken());
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());

        return ResponseEntity.ok(loginResponseDTO);
    }

    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(
            @CookieValue(name = "refreshToken") String oldTokenStr,
            HttpServletResponse httpServletResponse
    ) {

        RefreshToken oldToken = refreshTokenService.findByToken(oldTokenStr)
                .map(refreshTokenService::verifyExpiration)
                .orElseThrow(() -> new RefreshTokenException("RefreshToken is not in database"));

        RefreshToken newRefreshToken = refreshTokenService.createRefreshToken(oldToken.getUser().getUserId());

        ResponseCookie responseCookie = cookieUtils.createRefreshCookie(newRefreshToken.getToken());
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
        String newJwt = tokenService.generateToken(oldToken.getUser());
        return ResponseEntity.ok(new RefreshTokenResponse(newJwt));


    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> register(@RequestBody
                                                     RegisterUserRequest registerUserRequest, HttpServletResponse httpServletResponse) {
        userService.registerUser(registerUserRequest);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerUserRequest.username(),
                        registerUserRequest.password()));

        LoginResponseDTO loginResponseDTO = createLoginResponse(auth);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(loginResponseDTO.userId());
        ResponseCookie responseCookie = cookieUtils.createRefreshCookie(refreshToken.getToken());
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());


        return ResponseEntity.ok(loginResponseDTO);

    }


    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = "refreshToken", required = false) String refreshTokenStr,
            HttpServletResponse httpServletResponse
    ) {
        if (refreshTokenStr != null) {
            refreshTokenService.deleteRefreshToken(refreshTokenStr);
        }
        ResponseCookie cleanCookie = cookieUtils.getCleanResponseCookie();
        httpServletResponse.addHeader(HttpHeaders.SET_COOKIE, cleanCookie.toString());
        //logout metod, frontend rensar token
        return ResponseEntity.noContent().build();
    }

    /**
     * private Help method that receives an Authentication object and returns LoginResponseDTO
     *
     * @param auth Authentication-object used for user information and token
     * @return {@link LoginResponseDTO}
     */
    private LoginResponseDTO createLoginResponse(Authentication auth) {
        MyUserDetails userDetails = ((MyUserDetails) auth.getPrincipal());

        return new LoginResponseDTO(
                tokenService.generateToken(auth),
                userDetails.getUserId(),
                userDetails.getRole(),
                userDetails.getUsername()
        );


    }


}
