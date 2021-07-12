package spring.io.rest.recipes.unittests.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spring.io.rest.recipes.exceptions.ApiOperationException;
import spring.io.rest.recipes.models.entities.Recipe;
import spring.io.rest.recipes.models.entities.User;
import spring.io.rest.recipes.repositories.RecipeRepository;
import spring.io.rest.recipes.security.SecurityUtil;
import spring.io.rest.recipes.services.RecipeCRUDServices;
import spring.io.rest.recipes.services.dtos.entities.RecipeDto;
import spring.io.rest.recipes.services.dtos.mappers.RecipeEditMapper;
import spring.io.rest.recipes.services.dtos.mappers.RecipeMapper;
import spring.io.rest.recipes.services.util.RecipeServiceUtil;
import spring.io.rest.recipes.unittests.data.AbstractTestDataFactory;
import spring.io.rest.recipes.unittests.data.RecipeTestDataFactory;
import spring.io.rest.recipes.unittests.data.UserTestDataFactory;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeCRUDServicesTest {

    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private RecipeServiceUtil recipeServiceUtil;
    @Mock
    private RecipeMapper recipeMapper;
    @Mock
    private RecipeEditMapper recipeEditMapper;
    @Mock
    private SecurityUtil securityUtil;

    @InjectMocks
    RecipeCRUDServices recipeCRUDServices;

    private Recipe recipe;

    private RecipeDto recipeDto;

    private List<Recipe> recipeList;

    private List<RecipeDto> recipeDtoList;

    private User user;

    @BeforeEach
    void setUp() {
        RecipeTestDataFactory recipeTestDataFactory = AbstractTestDataFactory.getRecipeTestDataFactory();
        UserTestDataFactory userTestDataFactory = AbstractTestDataFactory.getUserTestDataFactory();
        recipe = recipeTestDataFactory.getRandomRecipe(1L);
        recipeDto = recipeTestDataFactory.getRandomRecipeDto(1L);
        recipeList = recipeTestDataFactory.getRandomRecipeList();
        recipeDtoList = recipeTestDataFactory.getRandomRecipeDtoList();
        user = userTestDataFactory.getRandomUser();
    }

    @Test
    void addRecipe() {
        when(recipeMapper.toRecipe(recipeDto)).thenReturn(recipe);
        when(recipeRepository.save(recipe)).thenReturn(recipe);
        when(securityUtil.getUserFromSubject()).thenReturn(user);
        assertEquals(recipeCRUDServices.addRecipe(recipeDto), recipe.getId());
    }

    @Test
    void modifyRecipePass() {
        when(recipeRepository.findById(recipeDto.getId())).thenReturn(Optional.ofNullable(recipe));
        recipeCRUDServices.modifyRecipe(recipeDto);
        verify(recipeServiceUtil, times(1)).saveUnavailableIngredients(any());
        verify(recipeServiceUtil, times(1)).addOrRemoveRecipeIngredients(any(), any());
        verify(recipeEditMapper, times(1)).updateRecipe(any(), any());
    }

    @Test
    void modifyRecipeFail() {
        when(recipeRepository.findById(recipeDto.getId())).thenReturn(Optional.empty());
        ApiOperationException exception = assertThrows(ApiOperationException.class, () -> {
            recipeCRUDServices.modifyRecipe(recipeDto);
        });
        assertEquals(exception.getMessage(), "Recipe is not present");
    }

    @Test
    void getRecipeWithIdPass() {
        when(recipeRepository.findById(recipeDto.getId())).thenReturn(Optional.ofNullable(recipe));
        when(recipeMapper.toRecipeDto(recipe)).thenReturn(recipeDto);
        assertEquals(recipeCRUDServices.getRecipeWithId(recipe.getId()), recipeDto);
    }

    @Test
    void getRecipeWithIdFail() {
        when(recipeRepository.findById(recipeDto.getId())).thenReturn(Optional.empty());
        ApiOperationException exception = assertThrows(ApiOperationException.class, () -> {
            recipeCRUDServices.getRecipeWithId(recipe.getId());
        });
        assertEquals(exception.getMessage(), "Recipe is not present");
    }

    @Test
    void getAllRecipes() {
        when(recipeRepository.findAll()).thenReturn(recipeList);
        when(recipeMapper.toRecipeDtoList(recipeList)).thenReturn(recipeDtoList);
        assertEquals(recipeCRUDServices.getAllRecipes(), recipeDtoList);
    }

    @Test
    void deleteRecipePass() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.ofNullable(recipe));
        recipeCRUDServices.deleteRecipe(recipe.getId());
        verify(recipeRepository, times(1)).deleteById(recipe.getId());
    }

    @Test
    void deleteRecipeFail() {
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.empty());
        ApiOperationException exception = assertThrows(ApiOperationException.class, () -> {
            recipeCRUDServices.deleteRecipe(recipe.getId());
        });
        assertEquals(exception.getMessage(), "Recipe is not present");
    }
}