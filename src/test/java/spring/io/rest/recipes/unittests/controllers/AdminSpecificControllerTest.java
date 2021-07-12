package spring.io.rest.recipes.unittests.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import spring.io.rest.recipes.controllers.admin.AdminSpecificController;
import spring.io.rest.recipes.security.AuthRequest;
import spring.io.rest.recipes.security.AuthResponse;
import spring.io.rest.recipes.services.CustomUserDetailsService;
import spring.io.rest.recipes.services.IngredientService;
import spring.io.rest.recipes.services.UserService;
import spring.io.rest.recipes.services.dtos.entities.IngredientDto;
import spring.io.rest.recipes.services.dtos.entities.UserDto;
import spring.io.rest.recipes.services.dtos.entities.UserProxyDto;
import spring.io.rest.recipes.services.dtos.entities.UserUpdateDto;
import spring.io.rest.recipes.services.dtos.entities.responses.ApiMessageResponse;
import spring.io.rest.recipes.unittests.data.AbstractTestDataFactory;
import spring.io.rest.recipes.unittests.data.IngredientTestDataFactory;
import spring.io.rest.recipes.unittests.data.UserTestDataFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminSpecificControllerTest {

    @Mock
    private IngredientService ingredientService;

    @Mock
    private UserService userService;

    @Mock
    private CustomUserDetailsService customUserDetailsService;

    @InjectMocks
    private AdminSpecificController adminSpecificController;

    private List<IngredientDto> ingredientDtoList;
    private IngredientDto ingredientDto;
    private UserProxyDto userProxyDto;
    private UserDto userDto;
    private UserUpdateDto userUpdateDto;
    private AuthRequest authRequest;
    private AuthResponse authResponse;
    private static final String jwtString = "RANDOM_STRING";

    @BeforeEach
    void setUp() {
        IngredientTestDataFactory ingredientTestDataFactory = AbstractTestDataFactory.getIngredientTestDataFactory();
        UserTestDataFactory userTestDataFactory = AbstractTestDataFactory.getUserTestDataFactory();
        ingredientDtoList = ingredientTestDataFactory.getRandomListOfIngredientDto();
        ingredientDto = ingredientTestDataFactory.getRandomIngredientDto(1L);
        userProxyDto = userTestDataFactory.getRandomUserProxyDto();
        userDto = userTestDataFactory.getRandomUserDto();
        userUpdateDto = userTestDataFactory.getRandomUserUpdateDto();
        authRequest = new AuthRequest(userDto.getEmail(), userDto.getPassword());
        authResponse = new AuthResponse(userProxyDto, jwtString);
    }

    @Test
    void addIngredients() {
        doNothing().when(ingredientService).addListOfIngredients(anyList());
        ResponseEntity<ApiMessageResponse> responseEntity = adminSpecificController.addIngredients(ingredientDtoList);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assert responseEntity.getBody() != null;
        assertEquals(responseEntity.getBody().getMessage(), ApiMessageResponse.defaultSuccessResponse().getMessage());
    }

    @Test
    void updateIngredient() {
        doNothing().when(ingredientService).updateIngredient(ingredientDto);
        ResponseEntity<ApiMessageResponse> responseEntity = adminSpecificController.updateIngredient(ingredientDto);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assert responseEntity.getBody() != null;
        assertEquals(responseEntity.getBody().getMessage(), ApiMessageResponse.defaultSuccessResponse().getMessage());
    }

    @Test
    void viewIngredients() {
        when(ingredientService.getIngredients()).thenReturn(ingredientDtoList);
        ResponseEntity<List<IngredientDto>> responseEntity = adminSpecificController.viewIngredients();
        assertEquals(responseEntity.getBody(), ingredientDtoList);
    }

    @Test
    void adminRegister() {
        when(userService.registerUser(any(UserDto.class))).thenReturn(userProxyDto);
        ResponseEntity<UserProxyDto> responseEntity = adminSpecificController.adminRegister(userDto);
        assertEquals(responseEntity.getBody(), userProxyDto);
    }

    @Test
    void adminUpdate() {
        doNothing().when(userService).updateUser(userUpdateDto);
        ResponseEntity<ApiMessageResponse> responseEntity = adminSpecificController.adminUpdate(userUpdateDto);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void login() {
        when(customUserDetailsService.login(any(AuthRequest.class))).thenReturn(authResponse);
        ResponseEntity<UserProxyDto> responseEntity = adminSpecificController.login(authRequest);
        assertEquals(responseEntity.getBody(), userProxyDto);
    }
}