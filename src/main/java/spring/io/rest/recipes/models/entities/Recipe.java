package spring.io.rest.recipes.models.entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;
import org.hibernate.Hibernate;
import spring.io.rest.recipes.enums.ItemType;

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
    private Integer serving;
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

        return Objects.equals(id, that.id);
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
