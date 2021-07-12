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
        List<Ingredient> unavailableIngredients;
        List<IngredientDto> unavailableIngredientDtos;

        recipeDto.getRecipeIngredients().forEach(recipeIngredientDto -> {
            IngredientDto ingredientDto = recipeIngredientDto.getIngredient();
            ingredientDto.setName(ingredientDto.getName().toUpperCase());

        });

        List<IngredientDto> allNullIdIngredientsInInput = recipeDto.getRecipeIngredients().stream().map(RecipeIngredientDto::getIngredient).filter(ingredient ->
                ingredient.getId() == null).collect(Collectors.toCollection(ArrayList::new));

        Map<String, Ingredient> nameToIngredientMapOfAvailIngredients = ingredientRepository.findIngredientsByNameIn(
                allNullIdIngredientsInInput.stream()
                //Map ingredient to corresponding to its name
                .map(IngredientDto::getName).collect(Collectors.toList())
                ) // function call ends
                .stream().collect(Collectors.toMap(Ingredient::getName, ingredient -> ingredient ));

        allNullIdIngredientsInInput.forEach(ingredientDto -> {
            String ingredientNameToSearch = ingredientDto.getName();
            if(nameToIngredientMapOfAvailIngredients.containsKey(ingredientNameToSearch)) {
                ingredientDto.setId(nameToIngredientMapOfAvailIngredients.get(ingredientNameToSearch).getId());
            }
        });

        unavailableIngredientDtos = allNullIdIngredientsInInput.stream().filter(ingredientDto -> ingredientDto.getId() == null).collect(Collectors.toList());

        unavailableIngredients = ingredientMapper.toIngredientList(unavailableIngredientDtos);

        ingredientRepository.saveAll(unavailableIngredients);

        IntStream.range(0, unavailableIngredients.size())
                .forEach(i -> unavailableIngredientDtos.get(i).setId(unavailableIngredients.get(i).getId()));
    }

    public void addOrRemoveRecipeIngredients(RecipeDto recipeDto, Recipe recipe) {
        List<RecipeIngredient> recipeIngredientList = recipe.getRecipeIngredients();
        List<RecipeIngredientDto> updatedRecipeIngredientList = recipeDto.getRecipeIngredients();

        List<RecipeIngredient> extraIngredients = updatedRecipeIngredientList.stream()
                .filter(recipeIngredientDto -> recipeIngredientDto.getId() == null)
                .map(recipeIngredientDto -> {
                    RecipeIngredient recipeIngredient = recipeIngredientMapper.toRecipeIngredient(recipeIngredientDto);
                    recipeIngredient.setRecipe(recipe);
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
