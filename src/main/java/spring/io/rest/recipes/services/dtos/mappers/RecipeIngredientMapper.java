package spring.io.rest.recipes.services.dtos.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import spring.io.rest.recipes.models.entities.Recipe;
import spring.io.rest.recipes.models.entities.RecipeIngredient;
import spring.io.rest.recipes.services.dtos.entities.RecipeIngredientDto;

import java.util.List;
import java.util.stream.IntStream;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", uses = IngredientMapper.class)
public abstract class RecipeIngredientMapper {

    public abstract List<RecipeIngredientDto> toRecipeIngredientDtoList(List<RecipeIngredient> recipeIngredients);
    @Mapping(target = "recipe", ignore = true)
    public abstract RecipeIngredient toRecipeIngredient(RecipeIngredientDto recipeIngredientDto);
    public abstract RecipeIngredientDto toRecipeIngredientDto(RecipeIngredient recipeIngredient);
    @BeanMapping(nullValueCheckStrategy = ALWAYS, nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "recipe", ignore = true)
    public abstract void updateRecipeIngredient(RecipeIngredientDto request,
                                                @MappingTarget RecipeIngredient recipeIngredient);
    public void updatedRecipeIngredientList(List<RecipeIngredientDto> recipeIngredientDtoList,
                                            List<RecipeIngredient> recipeIngredientList, Recipe recipe) {
        IntStream.range(0, recipeIngredientDtoList.size())
                .forEach(i -> {
                    updateRecipeIngredient(recipeIngredientDtoList.get(i), recipeIngredientList.get(i));
                    recipeIngredientList.get(i).setRecipe(recipe);
                });
    }

}
