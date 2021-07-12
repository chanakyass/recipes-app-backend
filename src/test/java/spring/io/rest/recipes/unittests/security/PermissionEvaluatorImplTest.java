package spring.io.rest.recipes.unittests.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import spring.io.rest.recipes.models.entities.Recipe;
import spring.io.rest.recipes.models.entities.User;
import spring.io.rest.recipes.repositories.RecipeRepository;
import spring.io.rest.recipes.repositories.UserRepository;
import spring.io.rest.recipes.security.PermissionEvaluatorImpl;
import spring.io.rest.recipes.services.dtos.entities.RecipeDto;
import spring.io.rest.recipes.services.dtos.entities.UserUpdateDto;
import spring.io.rest.recipes.unittests.data.AbstractTestDataFactory;
import spring.io.rest.recipes.unittests.data.RecipeTestDataFactory;
import spring.io.rest.recipes.unittests.data.UserTestDataFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionEvaluatorImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private PermissionEvaluatorImpl permissionEvaluator;

    private UserUpdateDto userUpdateDto;

    private User user;

    private RecipeDto recipeDto;

    private Recipe recipe;

    @BeforeEach
    void setUp() {

        UserTestDataFactory userTestDataFactory = AbstractTestDataFactory.getUserTestDataFactory();
        RecipeTestDataFactory recipeTestDataFactory = AbstractTestDataFactory.getRecipeTestDataFactory();


        userUpdateDto = userTestDataFactory.getRandomUserUpdateDto();
        user = userTestDataFactory.getRandomUser();


        recipeDto = recipeTestDataFactory.getRandomRecipeDto(1L);
        recipe = recipeTestDataFactory.getRandomRecipe(1L);

    }

    @Test
    void hasPermissionPassForRecipe() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.ofNullable(recipe));

        Authentication authentication = Mockito.mock(Authentication.class);
        UserDetails userDetails = Mockito.mock(UserDetails.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(user.getEmail());

        assertTrue(permissionEvaluator.hasPermission(authentication, recipeDto, null));
    }

    @Test
    void hasPermissionFailForRecipe_WhenRecipeUnavailable() {

        when(recipeRepository.findById(anyLong())).thenReturn(Optional.empty());
        //when(recipeRepository.findById(anyLong())).thenReturn(Optional.ofNullable(recipe));

        Authentication authentication = Mockito.mock(Authentication.class);
        UserDetails userDetails = Mockito.mock(UserDetails.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);

        AccessDeniedException accessDeniedException = assertThrows(AccessDeniedException.class, () -> permissionEvaluator.hasPermission(authentication, recipeDto, null));

        assertEquals(accessDeniedException.getMessage(), "Access is denied");
    }

    @Test
    void hasPermissionFailForRecipe_WhenUserUnavailable() {

        when(recipeRepository.findById(anyLong())).thenReturn(Optional.ofNullable(recipe));
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Authentication authentication = Mockito.mock(Authentication.class);
        UserDetails userDetails = Mockito.mock(UserDetails.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);

        AccessDeniedException accessDeniedException = assertThrows(AccessDeniedException.class, () -> permissionEvaluator.hasPermission(authentication, recipeDto, null));

        assertEquals(accessDeniedException.getMessage(), "Access is denied");
    }

    @Test
    void hasPermissionFailForRecipe_WhenUserNotSameAsLoggedInUser() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.ofNullable(recipe));
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        Authentication authentication = Mockito.mock(Authentication.class);
        UserDetails userDetails = Mockito.mock(UserDetails.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(userDetails.getUsername()).thenReturn("diff email");

        AccessDeniedException accessDeniedException = assertThrows(AccessDeniedException.class, () -> permissionEvaluator.hasPermission(authentication, recipeDto, null));

        assertEquals(accessDeniedException.getMessage(), "Access is denied");
    }

    @Test
    void hasPermissionPass_ForUserObject() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        Authentication authentication = Mockito.mock(Authentication.class);
        UserDetails userDetails = Mockito.mock(UserDetails.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(userDetails.getUsername()).thenReturn(user.getEmail());

        assertTrue(permissionEvaluator.hasPermission(authentication, userUpdateDto, null));
    }


    @Test
    void hasPermissionFailForUser_WhenUserUnavailable() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Authentication authentication = Mockito.mock(Authentication.class);
        UserDetails userDetails = Mockito.mock(UserDetails.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);

        AccessDeniedException accessDeniedException = assertThrows(AccessDeniedException.class, () -> permissionEvaluator.hasPermission(authentication, userUpdateDto, null));

        assertEquals(accessDeniedException.getMessage(), "Access is denied");
    }

    @Test
    void hasPermissionFailForUser_WhenUserNotSameAsLoggedInUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        Authentication authentication = Mockito.mock(Authentication.class);
        UserDetails userDetails = Mockito.mock(UserDetails.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(userDetails.getUsername()).thenReturn("diff email");

        AccessDeniedException accessDeniedException = assertThrows(AccessDeniedException.class, () -> permissionEvaluator.hasPermission(authentication, userUpdateDto, null));

        assertEquals(accessDeniedException.getMessage(), "Access is denied");
    }


    @Test
    void testHasPermissionPassForRecipe() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.ofNullable(recipe));

        Authentication authentication = Mockito.mock(Authentication.class);
        UserDetails userDetails = Mockito.mock(UserDetails.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(userDetails.getUsername()).thenReturn(user.getEmail());

        assertTrue(permissionEvaluator.hasPermission(authentication, 1L, "Recipe", null));
    }

    @Test
    void testHasPermissionFailForRecipe_WhenRecipeUnavailable() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.empty());

        Authentication authentication = Mockito.mock(Authentication.class);
        UserDetails userDetails = Mockito.mock(UserDetails.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);

        AccessDeniedException accessDeniedException = assertThrows(AccessDeniedException.class, () -> permissionEvaluator.hasPermission(authentication, 1L, "Recipe", null));

        assertEquals(accessDeniedException.getMessage(), "Access is denied");
    }

    @Test
    void testHasPermissionFailForRecipe_WhenLoggedInUserDifferent() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.ofNullable(recipe));

        Authentication authentication = Mockito.mock(Authentication.class);
        UserDetails userDetails = Mockito.mock(UserDetails.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(userDetails.getUsername()).thenReturn("diff email");

        AccessDeniedException accessDeniedException = assertThrows(AccessDeniedException.class, () -> permissionEvaluator.hasPermission(authentication, 1L, "Recipe", null));

        assertEquals(accessDeniedException.getMessage(), "Access is denied");
    }

    @Test
    void testHasPermissionFailForUser_WhenLoggedInUserDifferent() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

        Authentication authentication = Mockito.mock(Authentication.class);
        UserDetails userDetails = Mockito.mock(UserDetails.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(userDetails.getUsername()).thenReturn("diff email");

        AccessDeniedException accessDeniedException = assertThrows(AccessDeniedException.class, () -> permissionEvaluator.hasPermission(authentication, 1L, "User", null));

        assertEquals(accessDeniedException.getMessage(), "Access is denied");
    }

    @Test
    void testHasPermissionFailForUser_WhenUserUnavailable() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        Authentication authentication = Mockito.mock(Authentication.class);
        UserDetails userDetails = Mockito.mock(UserDetails.class);

        when(authentication.getPrincipal()).thenReturn(userDetails);

        AccessDeniedException accessDeniedException = assertThrows(AccessDeniedException.class, () -> permissionEvaluator.hasPermission(authentication, 1L, "User", null));

        assertEquals(accessDeniedException.getMessage(), "Access is denied");
    }
}