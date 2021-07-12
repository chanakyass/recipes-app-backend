package spring.io.rest.recipes.services.dtos.mappers;

import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;
import spring.io.rest.recipes.models.entities.Recipe;
import spring.io.rest.recipes.services.dtos.entities.RecipeDto;

import static org.mapstruct.NullValueCheckStrategy.ALWAYS;
import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public abstract class RecipeEditMapper {

    private RecipeIngredientMapper recipeIngredientMapper;

    @BeanMapping(nullValueCheckStrategy = ALWAYS, nullValuePropertyMappingStrategy = IGNORE)
    @Mapping(target = "recipeIngredients", ignore = true)
    public abstract void updateRecipe(RecipeDto request, @MappingTarget Recipe recipe);

    @AfterMapping
    public void afterUpdateRecipe(RecipeDto request, @MappingTarget Recipe recipe){

        recipeIngredientMapper.updatedRecipeIngredientList(request.getRecipeIngredients(), recipe.getRecipeIngredients(), recipe);

    }

    public RecipeIngredientMapper getRecipeIngredientMapper() {
        return recipeIngredientMapper;
    }

    @Autowired
    public void setRecipeIngredientMapper(RecipeIngredientMapper recipeIngredientMapper) {
        this.recipeIngredientMapper = recipeIngredientMapper;
    }
}
