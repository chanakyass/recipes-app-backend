package spring.io.rest.recipes.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import spring.io.rest.recipes.exceptions.ApiAccessException;
import spring.io.rest.recipes.models.entities.User;
import spring.io.rest.recipes.repositories.UserRepository;
import spring.io.rest.recipes.security.AuthRequest;
import spring.io.rest.recipes.security.AuthResponse;
import spring.io.rest.recipes.security.UserPrincipal;
import spring.io.rest.recipes.security.jwt.JwtTokenUtil;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepos;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepos, AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil) {
        this.userRepos = userRepos;
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    public AuthResponse login(AuthRequest authRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate
                    (new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            return AuthResponse.mapUserToAuthResponse(userPrincipal.getUser(), jwtTokenUtil.generateToken(userPrincipal));

        } catch (BadCredentialsException badCredentialsException) {
            throw new ApiAccessException(badCredentialsException.getLocalizedMessage());
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = Optional.ofNullable(userRepos.findUserByEmail(username)).orElseThrow(() -> new UsernameNotFoundException("Incorrect credentials"));
        return UserPrincipal.create(user);
    }
}
