package spring.io.rest.recipes.unittests.data;

import spring.io.rest.recipes.enums.ItemType;
import spring.io.rest.recipes.enums.UnitOfMeasurement;
import spring.io.rest.recipes.models.entities.Recipe;
import spring.io.rest.recipes.models.entities.RecipeIngredient;
import spring.io.rest.recipes.services.dtos.entities.RecipeDto;
import spring.io.rest.recipes.services.dtos.entities.RecipeIngredientDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RecipeTestDataFactory {

    private final UserTestDataFactory userTestDataFactory;
    private final IngredientTestDataFactory ingredientTestDataFactory;

    public RecipeTestDataFactory(UserTestDataFactory userTestDataFactory, IngredientTestDataFactory ingredientTestDataFactory) {
        this.userTestDataFactory = userTestDataFactory;
        this.ingredientTestDataFactory = ingredientTestDataFactory;
    }

    public Recipe getRandomRecipe(Long id) {
        Recipe recipe = new Recipe(id, "Paneer Tikka", "Punjabi food", LocalDateTime.now(), ItemType.VEG, 4, "random_address", "Random instructions",
                new ArrayList<>(List.of(
                        new RecipeIngredient(1L, null, ingredientTestDataFactory.getRandomIngredient(1L), 10.0, UnitOfMeasurement.GRAMS),
                        new RecipeIngredient(2L, null, ingredientTestDataFactory.getRandomIngredient(2L), 250.0, UnitOfMeasurement.MILLILITRES ))
                    )
        , userTestDataFactory.getRandomUser());
        for(RecipeIngredient recipeIngredient: recipe.getRecipeIngredients()) {
            recipeIngredient.setRecipe(recipe);
        }
        return recipe;
    }

    public RecipeDto getRandomRecipeDto(Long id) {
        Recipe recipe = getRandomRecipe(id);
        List<RecipeIngredientDto> recipeIngredientDtoList = new ArrayList<>();
        for(RecipeIngredient recipeIngredient: recipe.getRecipeIngredients()) {
            recipeIngredientDtoList.add(new RecipeIngredientDto(recipeIngredient.getId(),
                    ingredientTestDataFactory.getRandomIngredientDto(recipeIngredient.getIngredient().getId()),
                    recipeIngredient.getQuantity(), recipeIngredient.getUom()));
        }
        return new RecipeDto(recipe.getId(), recipe.getName(), recipe.getDescription(), recipe.getCreatedOn(), recipe.getItemType()
        , recipe.getServing(), recipe.getRecipeImageAddress(), recipe.getCookingInstructions(), recipeIngredientDtoList, userTestDataFactory.getRandomUserProxyDto());
    }

    public List<Recipe> getRandomRecipeList() {
        List<Recipe> recipeList = new ArrayList<>();
        for(long i=1; i<=10; i++) {
            Recipe recipe = getRandomRecipe(i);
            recipeList.add(recipe);
        }
        return recipeList;
    }

    public List<RecipeDto> getRandomRecipeDtoList() {
        List<RecipeDto> recipeDtoList = new ArrayList<>();
        for(Recipe recipe: getRandomRecipeList()) {
            recipeDtoList.add(getRandomRecipeDto(recipe.getId()));
        }
        return recipeDtoList;
    }

    public RecipeIngredient getRecipeIngredient(Long recipeIngredientId, Long ingredientId, Recipe recipe) {
        return new RecipeIngredient(recipeIngredientId, recipe, ingredientTestDataFactory.getRandomIngredient(ingredientId),
                100.0, UnitOfMeasurement.MILLILITRES);
    }
}
