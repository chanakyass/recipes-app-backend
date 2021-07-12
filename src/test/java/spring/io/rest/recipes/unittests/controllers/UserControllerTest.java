package spring.io.rest.recipes.unittests.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import spring.io.rest.recipes.controllers.UserController;
import spring.io.rest.recipes.services.UserService;
import spring.io.rest.recipes.services.dtos.entities.UserDto;
import spring.io.rest.recipes.services.dtos.entities.UserProxyDto;
import spring.io.rest.recipes.services.dtos.entities.UserUpdateDto;
import spring.io.rest.recipes.services.dtos.entities.responses.ApiMessageResponse;
import spring.io.rest.recipes.unittests.data.AbstractTestDataFactory;
import spring.io.rest.recipes.unittests.data.UserTestDataFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;
    @InjectMocks
    private UserController userController;
    private UserDto userDto;
    private UserProxyDto userProxyDto;
    private UserUpdateDto userUpdateDto;

    @BeforeEach
    void setUp() {
        UserTestDataFactory userTestDataFactory = AbstractTestDataFactory.getUserTestDataFactory();
        userDto = userTestDataFactory.getRandomUserDto();
        userProxyDto = userTestDataFactory.getRandomUserProxyDto();
        userUpdateDto = userTestDataFactory.getRandomUserUpdateDto();
    }

    @Test
    void update() {
        doNothing().when(userService).updateUser(any(UserUpdateDto.class));
        ResponseEntity<ApiMessageResponse> responseEntity = userController.update(userUpdateDto);
        assertNotNull(responseEntity);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assert responseEntity.getBody() != null;
        assertEquals(responseEntity.getBody().getMessage(), ApiMessageResponse.defaultSuccessResponse().getMessage());
    }

    @Test
    void getUser() {
        when(userService.getUserProxyById(anyLong())).thenReturn(userProxyDto);
        ResponseEntity<UserProxyDto> responseEntity = userController.getUser(userDto.getId());
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseEntity.getBody(), userProxyDto);
    }
}