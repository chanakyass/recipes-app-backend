package spring.io.rest.recipes.services.dtos.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.io.rest.recipes.enums.UnitOfMeasurement;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecipeIngredientDto {
    private Long id;
    private IngredientDto ingredient;
    private Double quantity;
    private UnitOfMeasurement uom;
}
