package spring.io.rest.recipes.unittests.data;

public class AbstractTestDataFactory {

    private final static UserTestDataFactory userTestDataFactory = new UserTestDataFactory();

    private final static IngredientTestDataFactory ingredientTestDataFactory = new IngredientTestDataFactory();

    private final static RecipeTestDataFactory recipeTestDataFactory = new RecipeTestDataFactory(userTestDataFactory, ingredientTestDataFactory);

    public static UserTestDataFactory getUserTestDataFactory() {
        return userTestDataFactory;
    }

    public static RecipeTestDataFactory getRecipeTestDataFactory() {
        return recipeTestDataFactory;
    }

    public static IngredientTestDataFactory getIngredientTestDataFactory() {
        return ingredientTestDataFactory;
    }
}
