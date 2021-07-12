package spring.io.rest.recipes.unittests.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spring.io.rest.recipes.enums.UnitOfMeasurement;
import spring.io.rest.recipes.models.entities.Ingredient;
import spring.io.rest.recipes.models.entities.Recipe;
import spring.io.rest.recipes.repositories.IngredientRepository;
import spring.io.rest.recipes.repositories.RecipeIngredientRepository;
import spring.io.rest.recipes.services.dtos.entities.IngredientDto;
import spring.io.rest.recipes.services.dtos.entities.RecipeDto;
import spring.io.rest.recipes.services.dtos.entities.RecipeIngredientDto;
import spring.io.rest.recipes.services.dtos.mappers.IngredientMapper;
import spring.io.rest.recipes.services.dtos.mappers.RecipeIngredientMapper;
import spring.io.rest.recipes.services.util.RecipeServiceUtil;
import spring.io.rest.recipes.unittests.data.AbstractTestDataFactory;
import spring.io.rest.recipes.unittests.data.IngredientTestDataFactory;
import spring.io.rest.recipes.unittests.data.RecipeTestDataFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeServiceUtilTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock private IngredientMapper ingredientMapper;

    @Mock
    RecipeIngredientMapper recipeIngredientMapper;

    @Mock
    private RecipeIngredientRepository recipeIngredientRepository;

    @InjectMocks
    private RecipeServiceUtil recipeServiceUtil;

    private RecipeDto recipeDto;
    private Recipe recipe;
    private IngredientTestDataFactory ingredientTestDataFactory;
    private RecipeTestDataFactory recipeTestDataFactory;

    @BeforeEach
    void setUp() {
        recipeTestDataFactory = AbstractTestDataFactory.getRecipeTestDataFactory();
        ingredientTestDataFactory = AbstractTestDataFactory.getIngredientTestDataFactory();
        recipeDto = recipeTestDataFactory.getRandomRecipeDto(null);
        recipe = recipeTestDataFactory.getRandomRecipe(null);
    }

    @Test
    void saveUnavailableIngredients() {

        // Setting up data starts here
        for(long i=11; i<=12; i++) {
            IngredientDto ingredientDto = ingredientTestDataFactory.getRandomIngredientDto(i);
            ingredientDto.setId(null);
            RecipeIngredientDto recipeIngredientDto = new RecipeIngredientDto(null, ingredientDto,
                    100.0, UnitOfMeasurement.MILLILITRES);
            recipeDto.getRecipeIngredients().add(recipeIngredientDto);
        }

        // Setting up data ends here


        long availableIngredientId = 11;
        Ingredient availableIngredient = ingredientTestDataFactory.getRandomIngredient(availableIngredientId);
        availableIngredient.setName(availableIngredient.getName().toUpperCase());
        when(ingredientRepository.findIngredientsByNameIn(anyList())).thenReturn(List.of(availableIngredient));

        long unavailableIngredientId = 12;
        Ingredient unavailableIngredient = ingredientTestDataFactory.getRandomIngredient(unavailableIngredientId);
        unavailableIngredient.setName(unavailableIngredient.getName().toUpperCase());
        unavailableIngredient.setId(null);
        when(ingredientMapper.toIngredientList(anyList())).thenReturn(List.of(unavailableIngredient));


        doAnswer(invocation -> {
            List<Ingredient> ingredientList = (List<Ingredient>) invocation.getArguments()[0];
            Random random = new Random();
            ingredientList.forEach(ingredient -> {
                if(ingredient.getId() == null) {
                    ingredient.setId(random.nextLong());
                }
            });
            return ingredientList;
        }).when(ingredientRepository).saveAll(anyIterable());

        recipeServiceUtil.saveUnavailableIngredients(recipeDto);

        for(RecipeIngredientDto recipeIngredientDto: recipeDto.getRecipeIngredients()) {
            assertNotNull(recipeIngredientDto.getIngredient().getId());
        }

    }

    @Test
    void addOrRemoveRecipeIngredients() {
        recipeDto.setId(1L);
        recipe.setId(1L);

        // Setting up data starts here

        recipeDto.getRecipeIngredients().remove(0);

        for(long i=11; i<=12; i++) {
            IngredientDto ingredientDto = ingredientTestDataFactory.getRandomIngredientDto(i);
            RecipeIngredientDto recipeIngredientDto = new RecipeIngredientDto(null, ingredientDto,
                    100.0, UnitOfMeasurement.MILLILITRES);
            recipeDto.getRecipeIngredients().add(recipeIngredientDto);
        }

        // Setting up data ends here

        when(recipeIngredientMapper.toRecipeIngredient(any(RecipeIngredientDto.class))).then(invocation -> {
            RecipeIngredientDto dto = invocation.getArgument(0);
            return recipeTestDataFactory.getRecipeIngredient(dto.getId(), dto.getIngredient().getId(), null);
        });

        when(recipeIngredientRepository.saveAll(anyList())).thenReturn(new ArrayList<>());
        doNothing().when(recipeIngredientRepository).deleteAllInBatch(anyList());

        recipeServiceUtil.addOrRemoveRecipeIngredients(recipeDto, recipe);

        IntStream.range(0, recipe.getRecipeIngredients().size())
                .forEach(i -> assertEquals(recipe.getRecipeIngredients().get(i).getId(), recipeDto.getRecipeIngredients().get(i).getId()));

    }
}