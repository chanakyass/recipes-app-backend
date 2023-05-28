package spring.io.rest.recipes.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.io.rest.recipes.services.IngredientService;
import spring.io.rest.recipes.services.RecipeCRUDServices;
import spring.io.rest.recipes.services.dtos.entities.IngredientDto;
import spring.io.rest.recipes.services.dtos.entities.RecipeDto;
import spring.io.rest.recipes.services.dtos.entities.responses.ApiMessageResponse;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("${app.uri.prefix}")
@RolesAllowed("ROLE_USER")
public class RecipesController {

    private final RecipeCRUDServices recipeCRUDServices;
    private final IngredientService ingredientService;

    @Autowired
    public RecipesController(RecipeCRUDServices recipeCRUDServices, IngredientService ingredientService) {
        this.recipeCRUDServices = recipeCRUDServices;
        this.ingredientService = ingredientService;
    }

    @PostMapping("recipe")
    public ResponseEntity<ApiMessageResponse> addRecipe(@RequestBody RecipeDto recipeDto){
        Long generatedId = recipeCRUDServices.addRecipe(recipeDto);
        return ResponseEntity.ok(ApiMessageResponse.defaultCreationSuccessResponse(generatedId));
    }

    @PutMapping("recipe")
    public ResponseEntity<ApiMessageResponse> updateRecipe(@RequestBody RecipeDto recipeDto){
        recipeCRUDServices.modifyRecipe(recipeDto);
        return ResponseEntity.ok(ApiMessageResponse.defaultSuccessResponse());
    }

    @DeleteMapping("recipe/{recipeId}")
    public ResponseEntity<ApiMessageResponse> deleteRecipe(@PathVariable("recipeId") Long recipeId){
        recipeCRUDServices.deleteRecipe(recipeId);
        return ResponseEntity.ok(ApiMessageResponse.defaultSuccessResponse());
    }

    @GetMapping("recipes")
    public ResponseEntity<List<RecipeDto>> getRecipes(){
        List<RecipeDto> recipeDtoList = recipeCRUDServices.getAllRecipes();
        return ResponseEntity.ok().body(recipeDtoList);
    }

    @GetMapping(value = "recipes", params = "searchString")
    public ResponseEntity<List<RecipeDto>> getRecipesWith(@RequestParam("searchString") String searchStr){
        List<RecipeDto> recipeDtoList = recipeCRUDServices.getAllRecipesWith(searchStr);
        return ResponseEntity.ok().body(recipeDtoList);
    }

    @GetMapping("recipe/{recipeId}")
    public ResponseEntity<RecipeDto> getRecipe(@PathVariable("recipeId") Long recipeId){
        return ResponseEntity.ok().body(recipeCRUDServices.getRecipeWithId(recipeId));

    }

    @GetMapping("ingredients")
    public ResponseEntity<List<IngredientDto>> getIngredientsStartingWith(@RequestParam(name="startsWith") String startsWith) {
        return ResponseEntity.ok().body(ingredientService.getAllIngredientsStartingWith(startsWith));
    }

    @GetMapping("ingredients/all")
    public ResponseEntity<List<IngredientDto>> getAllIngredients() {
        return ResponseEntity.ok().body(ingredientService.getIngredients());
    }

}
