package spring.io.rest.recipes.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import spring.io.rest.recipes.exceptions.ApiOperationException;
import spring.io.rest.recipes.models.entities.Recipe;
import spring.io.rest.recipes.repositories.RecipeRepository;
import spring.io.rest.recipes.security.SecurityUtil;
import spring.io.rest.recipes.services.dtos.entities.RecipeDto;
import spring.io.rest.recipes.services.dtos.mappers.RecipeEditMapper;
import spring.io.rest.recipes.services.dtos.mappers.RecipeMapper;
import spring.io.rest.recipes.services.util.RecipeServiceUtil;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeCRUDServices {
    private final RecipeRepository recipeRepository;
    private final RecipeMapper recipeMapper;
    private final RecipeEditMapper recipeEditMapper;
    private final RecipeServiceUtil recipeServiceUtil;
    private final SecurityUtil securityUtil;

    @Autowired
    public RecipeCRUDServices(RecipeRepository recipeRepository, RecipeMapper recipeMapper, RecipeEditMapper recipeEditMapper,
                              RecipeServiceUtil recipeServiceUtil, SecurityUtil securityUtil) {
        this.recipeRepository = recipeRepository;
        this.recipeMapper = recipeMapper;
        this.recipeEditMapper = recipeEditMapper;
        this.recipeServiceUtil = recipeServiceUtil;
        this.securityUtil = securityUtil;
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

    public List<RecipeDto> getAllRecipes(){
        List<Recipe> recipesList =  recipeRepository.findAll();
        return recipeMapper.toRecipeDtoList(recipesList);
    }

    @PreAuthorize("hasPermission(#recipeId, \"Recipe\", null)")
    public void deleteRecipe(Long recipeId) {
        recipeRepository.findById(recipeId).orElseThrow(() -> new ApiOperationException("Recipe is not present"));
        recipeRepository.deleteById(recipeId);
    }

}
