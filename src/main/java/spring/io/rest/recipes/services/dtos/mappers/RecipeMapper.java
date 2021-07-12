package spring.io.rest.recipes.services.dtos.mappers;

import org.mapstruct.Mapper;
import spring.io.rest.recipes.models.entities.Recipe;
import spring.io.rest.recipes.services.dtos.entities.RecipeDto;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, RecipeIngredientMapper.class})
public abstract class RecipeMapper {

    public abstract Recipe toRecipe(RecipeDto recipeDto);
    public abstract List<RecipeDto> toRecipeDtoList(List<Recipe> recipes);
    public abstract RecipeDto toRecipeDto(Recipe recipe);
}
