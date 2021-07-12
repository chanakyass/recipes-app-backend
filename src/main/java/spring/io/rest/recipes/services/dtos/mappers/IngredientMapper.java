package spring.io.rest.recipes.services.dtos.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import spring.io.rest.recipes.models.entities.Ingredient;
import spring.io.rest.recipes.services.dtos.entities.IngredientDto;

import java.util.List;

import static org.mapstruct.NullValuePropertyMappingStrategy.IGNORE;

@Mapper(componentModel = "spring")
public abstract class IngredientMapper {
    public abstract Ingredient toIngredient(IngredientDto ingredientDto);
    public abstract IngredientDto toIngredientDto(Ingredient ingredient);
    public abstract List<Ingredient> toIngredientList(List<IngredientDto>ingredientDtoList);
    public abstract List<IngredientDto> toIngredientDtoList(List<Ingredient> ingredientList);

    @BeanMapping(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, nullValuePropertyMappingStrategy = IGNORE)
    public abstract void editIngredient(IngredientDto request, @MappingTarget Ingredient ingredient);
}
