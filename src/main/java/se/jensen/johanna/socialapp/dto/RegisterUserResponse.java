package se.jensen.johanna.socialapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserResponse {
    private String email;
    private String username;
    private Long userId;
    private String token;


}
