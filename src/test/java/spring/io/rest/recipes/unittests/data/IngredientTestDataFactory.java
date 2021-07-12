package spring.io.rest.recipes.unittests.data;

import spring.io.rest.recipes.models.entities.Ingredient;
import spring.io.rest.recipes.services.dtos.entities.IngredientDto;

import java.util.ArrayList;
import java.util.List;

public class IngredientTestDataFactory {

    public IngredientTestDataFactory() {
    }

    public Ingredient getRandomIngredient(Long id) {
        return new Ingredient(id, "random_ingredient" + id, "random description " + id);
    }

    public List<Ingredient> getRandomListOfIngredients() {
        List<Ingredient> ingredientList = new ArrayList<>();
        for(long i=1; i<=10; i++) {
            ingredientList.add(getRandomIngredient(i));
        }
        return ingredientList;
    }

    public IngredientDto getRandomIngredientDto(Long id) {
        Ingredient ingredient = getRandomIngredient(id);
        return new IngredientDto(ingredient.getId(), ingredient.getName(), ingredient.getDescription());
    }

    public IngredientDto getUpdatedIngredientDto(Long id) {
        IngredientDto ingredientDto = getRandomIngredientDto(id);
        return new IngredientDto(ingredientDto.getId(), ingredientDto.getName()+" updated", ingredientDto.getDescription()+" updated");
    }

    public List<IngredientDto> getRandomListOfIngredientDto() {
        List<IngredientDto> ingredientDtoList = new ArrayList<>();
        for(long i=1; i<=10; i++) {
            ingredientDtoList.add(getRandomIngredientDto(i));
        }
        return ingredientDtoList;
    }

}
