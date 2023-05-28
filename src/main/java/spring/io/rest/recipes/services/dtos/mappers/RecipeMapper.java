package spring.io.rest.recipes.services.dtos.mappers;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import spring.io.rest.recipes.models.entities.Recipe;
import spring.io.rest.recipes.services.dtos.entities.RecipeDto;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, RecipeIngredientMapper.class})
public abstract class RecipeMapper {

    public abstract Recipe toRecipe(RecipeDto recipeDto);

    public abstract List<RecipeDto> toRecipeDtoList(List<Recipe> recipes);
    @Mapping(target = "recipeIngredients", ignore = true)
    @Mapping(target = "cuisines", ignore = true)
    @Mapping(target = "mealTypes", ignore = true)
    @Mapping(target = "dishCategories", ignore = true)
    public abstract RecipeDto toRecipeDto(Recipe recipe);


}
