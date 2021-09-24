package spring.io.rest.recipes.unittests.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import spring.io.rest.recipes.exceptions.ApiAccessException;
import spring.io.rest.recipes.models.entities.User;
import spring.io.rest.recipes.repositories.UserRepository;
import spring.io.rest.recipes.security.AuthRequest;
import spring.io.rest.recipes.security.AuthResponse;
import spring.io.rest.recipes.security.PayloadDetails;
import spring.io.rest.recipes.security.UserPrincipal;
import spring.io.rest.recipes.security.jwt.JwtTokenUtil;
import spring.io.rest.recipes.services.CustomUserDetailsService;
import spring.io.rest.recipes.unittests.data.AbstractTestDataFactory;
import spring.io.rest.recipes.unittests.data.UserTestDataFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {
    @Mock
    private UserRepository userRepos;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenUtil jwtTokenUtil;
    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    private AuthRequest authRequest;
    private AuthResponse authResponse;
    private UserPrincipal userPrincipal;
    private final String dummyJwt = "dummyjwt";

    @BeforeEach
    void setUp() {
        UserTestDataFactory userTestDataFactory = AbstractTestDataFactory.getUserTestDataFactory();
        User user = userTestDataFactory.getRandomUser();
        authRequest = new AuthRequest(user.getEmail(), user.getPassword());
        authResponse = new AuthResponse(userTestDataFactory.getRandomUserProxyDto(), dummyJwt);
        userPrincipal = UserPrincipal.create(user);
    }

    @Test
    void loginPass() {
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userPrincipal);
        when(jwtTokenUtil.generateToken(any(PayloadDetails.class))).thenReturn(dummyJwt);
        assertEquals(customUserDetailsService.login(authRequest), authResponse);
    }

    @Test
    void loginFail() {
        BadCredentialsException badCredentialsException = new BadCredentialsException("Bad credentials");
        when(authenticationManager.authenticate(any(Authentication.class))).thenThrow(badCredentialsException);
        ApiAccessException exception = assertThrows(ApiAccessException.class, () -> {
            customUserDetailsService.login(authRequest);
        });

        assertEquals(exception.getLocalizedMessage(), badCredentialsException.getLocalizedMessage());
    }

    @Test
    void loadUserByUsernamePass() {
        User user = userPrincipal.getUser();
        when(userRepos.findUserByEmail(anyString())).thenReturn(user);
        assertEquals(customUserDetailsService.loadUserByUsername(user.getEmail()), userPrincipal);
    }

    @Test
    void loadUserByUsernameFail() {
        User user = userPrincipal.getUser();
        when(userRepos.findUserByEmail(anyString())).thenReturn(null);
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
           customUserDetailsService.loadUserByUsername(user.getEmail());
        });
        assertEquals(exception.getMessage(), "Incorrect credentials");
    }
}