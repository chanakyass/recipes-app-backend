package spring.io.rest.recipes.unittests.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import spring.io.rest.recipes.controllers.AuthController;
import spring.io.rest.recipes.models.entities.User;
import spring.io.rest.recipes.security.AuthRequest;
import spring.io.rest.recipes.security.AuthResponse;
import spring.io.rest.recipes.services.CustomUserDetailsService;
import spring.io.rest.recipes.services.UserService;
import spring.io.rest.recipes.services.dtos.entities.UserDto;
import spring.io.rest.recipes.services.dtos.entities.UserProxyDto;
import spring.io.rest.recipes.unittests.data.AbstractTestDataFactory;
import spring.io.rest.recipes.unittests.data.UserTestDataFactory;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private CustomUserDetailsService customUserDetailsService;
    @Mock
    private UserService userService;
    @InjectMocks
    private AuthController authController;

    private AuthRequest authRequest;
    private AuthResponse authResponse;
    private String dummyJwt;
    private UserDto userDto;
    private UserProxyDto userProxyDto;
    private User user;

    @BeforeEach
    void setUp() {
        UserTestDataFactory userTestDataFactory = AbstractTestDataFactory.getUserTestDataFactory();
        user = userTestDataFactory.getRandomUser();
        userProxyDto = userTestDataFactory.getRandomUserProxyDto();
        userDto = userTestDataFactory.getRandomUserDto();
        byte [] array = new byte[16];
        new Random().nextBytes(array);
        dummyJwt = new String(array);
        authRequest = new AuthRequest(user.getEmail(), user.getPassword());
        authResponse = new AuthResponse(userProxyDto, dummyJwt);
    }

    @Test
    void loginUser() {
        when(customUserDetailsService.login(any(AuthRequest.class))).thenReturn(authResponse);
        ResponseEntity<UserProxyDto> responseEntity = authController.loginUser(authRequest);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        List<String> valueList = responseEntity.getHeaders().entrySet().stream()
                .filter(entry -> entry.getKey().equals(HttpHeaders.AUTHORIZATION)).map(Map.Entry::getValue).findAny().orElse(null);
        assertNotNull(valueList);
        assertEquals(valueList.get(0), dummyJwt);
    }

    @Test
    void register() {
        when(userService.registerUser(any(UserDto.class))).thenReturn(userProxyDto);
        ResponseEntity<UserProxyDto> responseEntity = authController.register(userDto);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseEntity.getBody(), userProxyDto);
    }
}