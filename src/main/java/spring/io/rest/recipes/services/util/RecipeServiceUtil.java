package spring.io.rest.recipes.services.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.io.rest.recipes.models.entities.Ingredient;
import spring.io.rest.recipes.models.entities.Recipe;
import spring.io.rest.recipes.models.entities.RecipeIngredient;
import spring.io.rest.recipes.repositories.IngredientRepository;
import spring.io.rest.recipes.repositories.RecipeIngredientRepository;
import spring.io.rest.recipes.services.dtos.entities.IngredientDto;
import spring.io.rest.recipes.services.dtos.entities.RecipeDto;
import spring.io.rest.recipes.services.dtos.entities.RecipeIngredientDto;
import spring.io.rest.recipes.services.dtos.mappers.IngredientMapper;
import spring.io.rest.recipes.services.dtos.mappers.RecipeIngredientMapper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class RecipeServiceUtil {
    private final IngredientRepository ingredientRepository;
    private final RecipeIngredientRepository recipeIngredientRepository;
    private final IngredientMapper ingredientMapper;
    private final RecipeIngredientMapper recipeIngredientMapper;

    @Autowired
    public RecipeServiceUtil(IngredientRepository ingredientRepository, IngredientMapper ingredientMapper, RecipeIngredientMapper recipeIngredientMapper, RecipeIngredientRepository recipeIngredientRepository) {
        this.ingredientRepository = ingredientRepository;
        this.ingredientMapper = ingredientMapper;
        this.recipeIngredientMapper = recipeIngredientMapper;
        this.recipeIngredientRepository = recipeIngredientRepository;
    }

    public void saveUnavailableIngredients(RecipeDto recipeDto) {
        List<Ingredient> ingredientsToBePersisted;
        List<IngredientDto> ingredientsToBePersistedDtos;

        recipeDto.getRecipeIngredients().forEach(recipeIngredientDto -> {
            IngredientDto ingredientDto = recipeIngredientDto.getIngredient();
            ingredientDto.setName(ingredientDto.getName().toUpperCase());

        });

        List<IngredientDto> newlyAddedIngredientsInInput = recipeDto.getRecipeIngredients().stream().map(RecipeIngredientDto::getIngredient).filter(ingredient ->
                ingredient.getId() == null).collect(Collectors.toCollection(ArrayList::new));

        Map<String, Ingredient> newlyAddedIngredientsAvailInDb = ingredientRepository.findIngredientsByNameIn(
                newlyAddedIngredientsInInput.stream()
                //Map ingredient to corresponding to its name
                .map(IngredientDto::getName).collect(Collectors.toList())
                ) // function call ends
                .stream().collect(Collectors.toMap(Ingredient::getName, ingredient -> ingredient ));

        newlyAddedIngredientsInInput.forEach(ingredientDto -> {
            String ingredientNameToSearch = ingredientDto.getName();
            if(newlyAddedIngredientsAvailInDb.containsKey(ingredientNameToSearch)) {
                ingredientDto.setId(newlyAddedIngredientsAvailInDb.get(ingredientNameToSearch).getId());
            }
        });

        ingredientsToBePersistedDtos = newlyAddedIngredientsInInput.stream().filter(ingredientDto -> ingredientDto.getId() == null).collect(Collectors.toList());

        ingredientsToBePersisted = ingredientMapper.toIngredientList(ingredientsToBePersistedDtos);

        ingredientRepository.saveAll(ingredientsToBePersisted);

        IntStream.range(0, ingredientsToBePersisted.size())
                .forEach(i -> ingredientsToBePersistedDtos.get(i).setId(ingredientsToBePersisted.get(i).getId()));
    }

    public void addOrRemoveRecipeIngredients(RecipeDto updatedRecipeDto, Recipe recipeFromDb) {
        List<RecipeIngredient> recipeIngredientList = recipeFromDb.getRecipeIngredients();
        List<RecipeIngredientDto> updatedRecipeIngredientList = updatedRecipeDto.getRecipeIngredients();

        List<RecipeIngredient> extraIngredients = updatedRecipeIngredientList.stream()
                .filter(recipeIngredientDto -> recipeIngredientDto.getId() == null)
                .map(recipeIngredientDto -> {
                    RecipeIngredient recipeIngredient = recipeIngredientMapper.toRecipeIngredient(recipeIngredientDto);
                    recipeIngredient.setRecipe(recipeFromDb);
                    return recipeIngredient;
                }).collect(Collectors.toList());

        Set<Long> updatedIngredientsSet = updatedRecipeIngredientList.stream()
                .map(RecipeIngredientDto::getId)
                .filter(Objects::nonNull).collect(Collectors.toSet());

        List<RecipeIngredient> removedIngredients = recipeIngredientList.stream()
                .filter(recipeIngredient -> !updatedIngredientsSet.contains(recipeIngredient.getId()))
                .collect(Collectors.toList());

        recipeIngredientRepository.deleteAllInBatch(removedIngredients);
        recipeIngredientRepository.saveAll(extraIngredients);

        recipeIngredientList.removeAll(removedIngredients);
        recipeIngredientList.addAll(extraIngredients);
    }

}
