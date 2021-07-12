package spring.io.rest.recipes.unittests.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import spring.io.rest.recipes.controllers.RecipesController;
import spring.io.rest.recipes.services.IngredientService;
import spring.io.rest.recipes.services.RecipeCRUDServices;
import spring.io.rest.recipes.services.dtos.entities.IngredientDto;
import spring.io.rest.recipes.services.dtos.entities.RecipeDto;
import spring.io.rest.recipes.services.dtos.entities.responses.ApiMessageResponse;
import spring.io.rest.recipes.unittests.data.AbstractTestDataFactory;
import spring.io.rest.recipes.unittests.data.IngredientTestDataFactory;
import spring.io.rest.recipes.unittests.data.RecipeTestDataFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipesControllerTest {

    @Mock
    private RecipeCRUDServices recipeCRUDServices;

    @Mock
    private IngredientService ingredientService;

    @InjectMocks
    private RecipesController recipesController;

    private RecipeDto recipeDto;
    private List<RecipeDto> recipeDtoList;
    private List<IngredientDto> ingredientDtoList;

    @BeforeEach
    void setUp() {
        RecipeTestDataFactory recipeTestDataFactory = AbstractTestDataFactory.getRecipeTestDataFactory();
        IngredientTestDataFactory ingredientTestDataFactory = AbstractTestDataFactory.getIngredientTestDataFactory();
        recipeDto = recipeTestDataFactory.getRandomRecipeDto(1L);
        recipeDtoList = recipeTestDataFactory.getRandomRecipeDtoList();
        ingredientDtoList = ingredientTestDataFactory.getRandomListOfIngredientDto();

    }

    @Test
    void addRecipe() {
        when(recipeCRUDServices.addRecipe(any(RecipeDto.class))).thenReturn(recipeDto.getId());
        ResponseEntity<ApiMessageResponse> responseEntity = recipesController.addRecipe(recipeDto);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertNotNull(responseEntity.getBody());
        assertEquals(responseEntity.getBody().getGeneratedId(), recipeDto.getId());
    }

    @Test
    void updateRecipe() {
        doNothing().when(recipeCRUDServices).modifyRecipe(any(RecipeDto.class));
        ResponseEntity<ApiMessageResponse> responseEntity = recipesController.updateRecipe(recipeDto);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void deleteRecipe() {
        doNothing().when(recipeCRUDServices).deleteRecipe(anyLong());
        ResponseEntity<ApiMessageResponse> responseEntity = recipesController.deleteRecipe(recipeDto.getId());
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
    }

    @Test
    void getRecipes() {
        when(recipeCRUDServices.getAllRecipes()).thenReturn(recipeDtoList);
        ResponseEntity<List<RecipeDto>> responseEntity = recipesController.getRecipes();
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseEntity.getBody(), recipeDtoList);
    }

    @Test
    void getRecipe() {
        when(recipeCRUDServices.getRecipeWithId(anyLong())).thenReturn(recipeDto);
        ResponseEntity<RecipeDto> responseEntity = recipesController.getRecipe(recipeDto.getId());
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseEntity.getBody(), recipeDto);
    }

    @Test
    void getIngredientsStartingWith() {
        when(ingredientService.getAllIngredientsStartingWith(anyString())).thenReturn(ingredientDtoList);
        ResponseEntity<List<IngredientDto>> responseEntity = recipesController.getIngredientsStartingWith("random string");
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseEntity.getBody(), ingredientDtoList);
    }

    @Test
    void getAllIngredients() {
        when(ingredientService.getIngredients()).thenReturn(ingredientDtoList);
        ResponseEntity<List<IngredientDto>> responseEntity = recipesController.getAllIngredients();
        assertEquals(responseEntity.getStatusCode(), HttpStatus.OK);
        assertEquals(responseEntity.getBody(), ingredientDtoList);
    }


}