package se.jensen.johanna.socialapp.service;

import lombok.RequiredArgsConstructor;
import se.jensen.johanna.socialapp.model.User;
import se.jensen.johanna.socialapp.repository.UserRepository;
import se.jensen.johanna.socialapp.security.MyUserDetails;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found " + username));
        System.out.println("DEBUG: Försöker logga in användare: " + username);
        System.out.println("DEBUG: Lösenord i Java-objektet: " + user.getPassword());
        return new MyUserDetails(user);

    }
}
