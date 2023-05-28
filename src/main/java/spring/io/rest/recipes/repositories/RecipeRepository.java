package spring.io.rest.recipes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import spring.io.rest.recipes.enums.MealType;
import spring.io.rest.recipes.models.entities.Recipe;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Override
    @Query("select distinct r as recipe from Recipe r join fetch r.recipeIngredients ri join fetch ri.ingredient join fetch r.user where r.id=:id")
    Optional<Recipe> findById(Long id);
    @Query("select distinct r as recipe from Recipe r join fetch r.recipeIngredients ri join fetch ri.ingredient join fetch r.user where r.id in (:recipeIds)")
    List<Recipe> findRecipesByIdsIn(@Param("recipeIds") List<Long> recipeIds);

    @Query("select distinct r from Recipe r join fetch r.user join fetch r.mealTypes")
    List<Recipe> findRecipeMealTypes();
    @Query("select distinct r from Recipe r join fetch r.user join fetch r.mealTypes where r.id in (:recipeIds)")
    List<Recipe> findRecipeMealTypesWithIdsIn(@Param("recipeIds") List<Long> recipeIds);

    @Query("select distinct r from Recipe r join fetch r.user join fetch r.mealTypes m where m in :mealTypes")
    List<Recipe> findRecipesByMealTypes(List<MealType> mealTypes);

    @Query("select distinct r from Recipe r join fetch r.user join fetch r.cuisines")
    List<Recipe> findRecipeCuisines();

    @Query("select distinct r from Recipe r join fetch r.user join fetch r.cuisines where r.id in (:recipeIds)")
    List<Recipe> findRecipeCuisinesWithIdsIn(@Param("recipeIds") List<Long> recipeIds);

    @Query("select distinct r from Recipe r join fetch r.user join fetch r.cuisines c where lower(c) like %:searchStr%")
    List<Recipe> findRecipeBySearchingCuisinesWith(@Param("searchStr") String searchStr);

    @Query("select distinct r from Recipe r join fetch r.user join fetch r.dishCategories")
    List<Recipe> findRecipeDishCategories();

    @Query("select distinct r from Recipe r join fetch r.user join fetch r.dishCategories where r.id in (:recipeIds)")
    List<Recipe> findRecipeDishCategoriesWithIdsIn(@Param("recipeIds") List<Long> recipeIds);

    @Query("select distinct r from Recipe r join fetch r.user join fetch r.dishCategories dc where lower(dc) like %:searchStr%")
    List<Recipe> findRecipeBySearchingDishCategoriesWith(@Param("searchStr") String searchStr);

    @Override
    @Query("select distinct r from Recipe r join fetch r.recipeIngredients ri join fetch ri.ingredient join fetch r.user")
    List<Recipe> findAll();
    @Query("select distinct r from Recipe r join fetch r.recipeIngredients ri join fetch ri.ingredient join fetch r.user " +
            "where lower(r.name) like %:searchStr% OR lower(r.description) like %:searchStr% OR lower(r.cookingInstructions) like %:searchStr%")
    List<Recipe> findRecipesBySearchingNonListFieldsWith(@Param("searchStr") String searchStr);
}
