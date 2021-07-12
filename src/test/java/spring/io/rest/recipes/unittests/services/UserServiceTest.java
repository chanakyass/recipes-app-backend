package spring.io.rest.recipes.unittests.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import spring.io.rest.recipes.exceptions.ApiOperationException;
import spring.io.rest.recipes.models.entities.User;
import spring.io.rest.recipes.repositories.UserRepository;
import spring.io.rest.recipes.services.UserService;
import spring.io.rest.recipes.services.dtos.entities.UserDto;
import spring.io.rest.recipes.services.dtos.entities.UserProxyDto;
import spring.io.rest.recipes.services.dtos.entities.UserUpdateDto;
import spring.io.rest.recipes.services.dtos.mappers.UserMapper;
import spring.io.rest.recipes.unittests.data.AbstractTestDataFactory;
import spring.io.rest.recipes.unittests.data.UserTestDataFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User currentUser;

    private UserDto currentUserDto;

    private UserProxyDto currentUserProxyDto;

    private UserUpdateDto userUpdateDto;

    @BeforeEach
    void setUp() {
        UserTestDataFactory userTestDataFactory = AbstractTestDataFactory.getUserTestDataFactory();
        currentUser = userTestDataFactory.getRandomUser();
        currentUserDto = userTestDataFactory.getRandomUserDto();
        currentUserProxyDto = userTestDataFactory.getRandomUserProxyDto();
        userUpdateDto = userTestDataFactory.getRandomUserUpdateDto();
    }

    @Test
    void registerUserPass() {
        when(userMapper.toUser(currentUserDto)).thenReturn(currentUser);

        when(userRepository.findUserByEmail(currentUser.getEmail())).thenReturn(null);

        when(passwordEncoder.encode(currentUser.getPassword())).thenReturn(currentUser.getPassword());

        when(userRepository.save(currentUser))
                .thenReturn(currentUser);

        when(userMapper.toUserProxyDto(currentUser)).thenReturn(currentUserProxyDto);

        assertEquals(currentUserProxyDto, userService.registerUser(currentUserDto));
    }

    @Test
    void registerUserFail() {
        when(userRepository.findUserByEmail(currentUser.getEmail())).thenReturn(currentUser);

        ApiOperationException apiOperationException = assertThrows(ApiOperationException.class, () -> userService.registerUser(currentUserDto));
        assertEquals(apiOperationException.getMessage(), "User already exists");

    }

    @Test
    void getUserByIdPass() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(currentUser));
        assertEquals(userRepository.findById(currentUserDto.getId()).get(), currentUser);
    }

    @Test
    void getUserByIdFail() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        ApiOperationException exception = assertThrows(ApiOperationException.class, () -> {
            userService.getUserById(anyLong());
        });
        assertEquals(exception.getMessage(), "No such user");
    }

    @Test
    void getUserProxyByIdPass() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(currentUser));
        when(userMapper.toUserProxyDto(currentUser)).thenReturn(currentUserProxyDto);
        assertEquals(userService.getUserProxyById(anyLong()), currentUserProxyDto);
    }

    @Test
    void getUserProxyByIdFail() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        ApiOperationException exception = assertThrows(ApiOperationException.class, () -> {
            userService.getUserProxyById(anyLong());
        });
        assertEquals(exception.getMessage(), "No such user");
    }

    @Test
    void getUserByEmailPass() {
        when(userRepository.findUserByEmail(anyString())).thenReturn(currentUser);
        assertEquals(userService.getUserByEmail(currentUser.getEmail()), currentUser);
    }

    @Test
    void getUserByEmailFail() {
        when(userRepository.findUserByEmail(anyString())).thenReturn(null);
        ApiOperationException exception = assertThrows(ApiOperationException.class, () -> {
            userService.getUserByEmail(currentUser.getEmail());
        });
        assertEquals(exception.getMessage(), "No such user");
    }

    @Test
    void updateUserPass() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(currentUser));
        userService.updateUser(userUpdateDto);
        verify(userMapper, times(1)).toUser(any(UserUpdateDto.class), any(User.class));
    }
}