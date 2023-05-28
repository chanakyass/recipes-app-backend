package spring.io.rest.recipes.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import spring.io.rest.recipes.enums.MealType;
import spring.io.rest.recipes.exceptions.ApiOperationException;
import spring.io.rest.recipes.models.entities.Recipe;
import spring.io.rest.recipes.models.entities.RecipeIngredient;
import spring.io.rest.recipes.repositories.RecipeRepository;
import spring.io.rest.recipes.security.SecurityUtil;
import spring.io.rest.recipes.services.dtos.entities.RecipeDto;
import spring.io.rest.recipes.services.dtos.entities.RecipeIngredientDto;
import spring.io.rest.recipes.services.dtos.mappers.RecipeEditMapper;
import spring.io.rest.recipes.services.dtos.mappers.RecipeIngredientMapper;
import spring.io.rest.recipes.services.dtos.mappers.RecipeMapper;
import spring.io.rest.recipes.services.util.RecipeServiceUtil;

import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

interface IApply<T, U> {
    List<T> methodToInvoke(List<U> param);
}

@Service
public class RecipeCRUDServices {
    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;

    private final RecipeIngredientMapper recipeIngredientMapper;
    private final RecipeEditMapper recipeEditMapper;
    private final RecipeServiceUtil recipeServiceUtil;
    private final SecurityUtil securityUtil;

    private final Field recipeIngredientsField;
    private final Field cuisinesField;
    private final Field dishCategoriesField;
    private final Field mealTypesField;

    @Autowired
    public RecipeCRUDServices(RecipeRepository recipeRepository, RecipeMapper recipeMapper, RecipeEditMapper recipeEditMapper, RecipeIngredientMapper recipeIngredientMapper,
                              RecipeServiceUtil recipeServiceUtil, SecurityUtil securityUtil) {
        this.recipeRepository = recipeRepository;
        this.recipeMapper = recipeMapper;
        this.recipeEditMapper = recipeEditMapper;
        this.recipeIngredientMapper = recipeIngredientMapper;
        this.recipeServiceUtil = recipeServiceUtil;
        this.securityUtil = securityUtil;
        try {
            this.recipeIngredientsField = Recipe.class.getDeclaredField("recipeIngredients");
            this.cuisinesField = Recipe.class.getDeclaredField("cuisines");
            this.dishCategoriesField = Recipe.class.getDeclaredField("dishCategories");
            this.mealTypesField = Recipe.class.getDeclaredField("mealTypes");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public Long addRecipe(RecipeDto recipeDto) {
        Optional.ofNullable(recipeDto).orElseThrow(()-> new ApiOperationException("Wrong format"));
        recipeServiceUtil.saveUnavailableIngredients(recipeDto);
        Recipe recipe = recipeMapper.toRecipe(recipeDto);
        recipe.setUser(securityUtil.getUserFromSubject());
        Recipe newRecipe = recipeRepository.save(recipe);
        return newRecipe.getId();
    }

    @Transactional
    @PreAuthorize("hasPermission(#recipeDto, null)")
    public void modifyRecipe(RecipeDto recipeDto) {
        Recipe recipe = recipeRepository.findById(recipeDto.getId()).orElseThrow(() -> new ApiOperationException("Recipe is not present"));
        recipeServiceUtil.saveUnavailableIngredients(recipeDto);
        recipeServiceUtil.addOrRemoveRecipeIngredients(recipeDto, recipe);
        recipeEditMapper.updateRecipe(recipeDto, recipe);
    }

    public RecipeDto getRecipeWithId(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new ApiOperationException("Recipe is not present"));
        return recipeMapper.toRecipeDto(recipe);
    }

    private void mergeUnavailablePropsFromSrcToDest(Map<Field, List<Recipe>> mapOfRecipesHavingValueOfMissingProps, List<RecipeDto> recipeDtoList) {
        //Map<Long, Recipe> recipeMap = recipeList.stream().collect(Collectors.toMap(Recipe::getId, recipe -> recipe));
        mapOfRecipesHavingValueOfMissingProps.keySet().forEach(missingProp -> {
            missingProp.setAccessible(true);
            List<Recipe> recipeListHavingValuesOfMissingProp = mapOfRecipesHavingValueOfMissingProps.get(missingProp);
            Map<Long, Recipe> idToRecipeMap = recipeListHavingValuesOfMissingProp.stream().collect(Collectors.toMap(Recipe::getId, recipe -> recipe));
            for (RecipeDto destRecipeDto : recipeDtoList) {
                if (idToRecipeMap.containsKey(destRecipeDto.getId())) {
                    try {
                        Recipe recipeHavingValueOfMissingProp = idToRecipeMap.get(destRecipeDto.getId());
                        Object missingValueOfProp = missingProp.get(recipeHavingValueOfMissingProp);

                        Field missingPropInRecipeDto = RecipeDto.class.getDeclaredField(missingProp.getName());
                        missingPropInRecipeDto.setAccessible(true);
                        if (missingPropInRecipeDto.getName().equals("recipeIngredients")) {
                            missingValueOfProp = recipeIngredientMapper.toRecipeIngredientDtoList((List<RecipeIngredient>) missingValueOfProp);
                        }
                        missingPropInRecipeDto.set(destRecipeDto, missingValueOfProp);
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    @Transactional
    private void createMapOfMissingPropsToRecipeList(List<Field> missingProps, List<Recipe> srcList, Map<Field, List<Recipe>> mapFieldToRecipeList) {
        List<Long> recipeIdList = srcList.stream().map(Recipe::getId).collect(Collectors.toList());
        Map<Field, IApply<Recipe, Long>> fieldToIApplyMap = Map.ofEntries(Map.entry(recipeIngredientsField, recipeRepository::findRecipesByIdsIn),
                Map.entry(cuisinesField, recipeRepository::findRecipeCuisinesWithIdsIn),
                Map.entry(dishCategoriesField, recipeRepository::findRecipeDishCategoriesWithIdsIn),
                Map.entry(mealTypesField, recipeRepository:: findRecipeMealTypesWithIdsIn));
        for(Field field: missingProps) {
            IApply<Recipe, Long> apply = fieldToIApplyMap.get(field);
            List<Recipe> recipeListWithMissingProps = (apply != null) ? apply.methodToInvoke(recipeIdList) : null;
            if (recipeListWithMissingProps != null) {
                mapFieldToRecipeList.put(field, recipeListWithMissingProps);
            }
        }
    }

    @Transactional
    public List<RecipeDto> getAllRecipes(){
        List<Recipe> recipesList =  recipeRepository.findAll();
        List<Recipe> recipesWithMealTypes = recipeRepository.findRecipeMealTypes();
        List<Recipe> recipesWithCuisines = recipeRepository.findRecipeCuisines();
        List<Recipe> recipesWithDishCategories = recipeRepository.findRecipeDishCategories();

        Map<Field, List<Recipe>> fieldToRecipeListMap = new HashMap<>();
        fieldToRecipeListMap.put(mealTypesField, recipesWithMealTypes);
        fieldToRecipeListMap.put(cuisinesField, recipesWithCuisines);
        fieldToRecipeListMap.put(dishCategoriesField, recipesWithDishCategories);
        mergeUnavailablePropsFromSrcToDest(fieldToRecipeListMap, recipeMapper.toRecipeDtoList(recipesList));
        return recipeMapper.toRecipeDtoList(recipesList);
    }

    private List<Recipe> getIntersectingRecipes(List<List<Recipe>> listOfLists) {
        return listOfLists.stream().reduce((overallResLL, currLL) -> {
            Set<Recipe> currSet = new HashSet<>(currLL);
            return overallResLL.stream().filter(currSet::contains).collect(Collectors.toList());
        }).orElse(new ArrayList<>());
    }

    @Transactional
    public List<RecipeDto> getAllRecipesWith(String searchString) throws ApiOperationException{
        List<List<Recipe>> listOfListsRecipes = new ArrayList<>();
        String[] searchStrings = new String[searchString.split(" ").length];
        Arrays.stream(searchString.split(" ")).map(String::toLowerCase).collect(Collectors.toList()).toArray(searchStrings);
        Map<Field, List<Recipe>> mapFieldToRecipeList = new HashMap<>();

        for (String searchStr: searchStrings) {
            List<Recipe> recipesWithNonListFieldsHavingStr = recipeRepository.findRecipesBySearchingNonListFieldsWith(searchStr);

            List<MealType> mealTypesBySearchString = Arrays.stream(MealType.values()).filter(mealType -> mealType.name().toLowerCase().contains(searchStr.toLowerCase())).collect(Collectors.toList());
            List<Recipe> recipesWithMealTypesHavingStr = null;
            if (!mealTypesBySearchString.isEmpty()) {
                recipesWithMealTypesHavingStr = recipeRepository.findRecipesByMealTypes(mealTypesBySearchString);
            }

            List<Recipe> recipesWithCuisinesHavingStr = recipeRepository.findRecipeBySearchingCuisinesWith(searchStr);

            List<Recipe> recipesWithDishCategoriesHavingStr = recipeRepository.findRecipeBySearchingDishCategoriesWith(searchStr);


            HashSet<Recipe> recipesSetWithSearchStr = new HashSet<>();
            if (recipesWithMealTypesHavingStr != null) {
                recipesSetWithSearchStr.addAll(recipesWithMealTypesHavingStr);
            }
            recipesSetWithSearchStr.addAll(recipesWithNonListFieldsHavingStr);
            recipesSetWithSearchStr.addAll(recipesWithCuisinesHavingStr);
            recipesSetWithSearchStr.addAll(recipesWithDishCategoriesHavingStr);

            List<Recipe> recipesWithSearchStr = new ArrayList<>(recipesSetWithSearchStr);

            listOfListsRecipes.add(recipesWithSearchStr);
        }
        List<Recipe> finalListOfRecipesWithSearchStr = getIntersectingRecipes(listOfListsRecipes);
        createMapOfMissingPropsToRecipeList(List.of(recipeIngredientsField, mealTypesField, cuisinesField, dishCategoriesField), finalListOfRecipesWithSearchStr, mapFieldToRecipeList);
        List<RecipeDto> recipeDtoList = recipeMapper.toRecipeDtoList(finalListOfRecipesWithSearchStr);
        mergeUnavailablePropsFromSrcToDest(mapFieldToRecipeList, recipeDtoList);
        return recipeDtoList;
    }

    @PreAuthorize("hasPermission(#recipeId, \"Recipe\", null)")
    public void deleteRecipe(Long recipeId) {
        recipeRepository.findById(recipeId).orElseThrow(() -> new ApiOperationException("Recipe is not present"));
        recipeRepository.deleteById(recipeId);
    }

}
