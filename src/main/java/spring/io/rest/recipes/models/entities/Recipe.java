package spring.io.rest.recipes.models.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import spring.io.rest.recipes.enums.ItemType;
import spring.io.rest.recipes.enums.MealType;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recipes")
public class Recipe {
    @Id
    @SequenceGenerator(name = "recipe_sequence", sequenceName = "recipe_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recipe_sequence")
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdOn;
    @Enumerated(value = EnumType.ORDINAL)
    private ItemType itemType;
    @ElementCollection
    @CollectionTable(name="recipe_meal_types", joinColumns=@JoinColumn(name="recipe_id",
            foreignKey = @ForeignKey(name = "fk_meal_type_recipe_id")))
    @Column(name="meal_type")
    private List<MealType> mealTypes;
    private Integer serving;
    @ElementCollection
    @CollectionTable(name="recipe_cuisines", joinColumns=@JoinColumn(name="recipe_id",
            foreignKey = @ForeignKey(name = "fk_cuisine_recipe_id")))
    @Column(name="cuisine")
    private List<String> cuisines;
    @ElementCollection
    @CollectionTable(name="recipe_categories", joinColumns=@JoinColumn(name="recipe_id",
            foreignKey = @ForeignKey(name = "fk_category_recipe_id")))
    @Column(name="category")
    private List<String> dishCategories;
    private String recipeImageAddress;
    @Column(length = 65536)
    private String cookingInstructions;
    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<RecipeIngredient> recipeIngredients;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", foreignKey = @ForeignKey(name = "fk_recipe_user_id"), nullable = false)
    private User user;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Recipe that = (Recipe) o;
        return that.hashCode() == this.hashCode();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getCreatedOn(), getItemType());
    }

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public List<RecipeIngredient> getRecipeIngredients() {
        return recipeIngredients;
    }

    public void setRecipeIngredients(List<RecipeIngredient> recipeIngredients) {
        recipeIngredients.forEach(ingredient-> ingredient.setRecipe(this));
        this.recipeIngredients = recipeIngredients;
    }


}
