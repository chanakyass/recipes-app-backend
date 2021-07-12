package spring.io.rest.recipes.models.entities;

import lombok.*;
import org.hibernate.Hibernate;
import spring.io.rest.recipes.enums.UnitOfMeasurement;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recipe_ingredients")
public class RecipeIngredient {
    @Id
    @SequenceGenerator(name = "recipe_ingredient_sequence", sequenceName = "recipe_ingredient_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "recipe_ingredient_sequence")
    private Long id;
    @ManyToOne
    @JoinColumn(name = "recipe_id", foreignKey = @ForeignKey(name = "fk_recipe_id"), nullable = false)
    private Recipe recipe;
    @ManyToOne
    @JoinColumn(name = "ingredient_id", foreignKey = @ForeignKey(name = "fk_ingredient_id"),
            nullable = false)
    private Ingredient ingredient;
    private Double quantity;
    @Enumerated(value = EnumType.ORDINAL)
    private UnitOfMeasurement uom;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        RecipeIngredient that = (RecipeIngredient) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getIngredient(), getRecipe(), getQuantity(), getUom());
    }
}