package spring.io.rest.recipes.services.dtos.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import spring.io.rest.recipes.enums.ItemType;
import spring.io.rest.recipes.enums.MealType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RecipeDto {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdOn;
    private ItemType itemType;
    private MealType mealType;
    private Integer serving;
    private String cuisine;
    private String course;
    private String recipeImageAddress;
    private String cookingInstructions;
    private List<RecipeIngredientDto> recipeIngredients;
    private UserProxyDto user;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }
}
