package spring.io.rest.recipes.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring.io.rest.recipes.exceptions.ApiOperationException;
import spring.io.rest.recipes.models.entities.Ingredient;
import spring.io.rest.recipes.repositories.IngredientRepository;
import spring.io.rest.recipes.services.dtos.entities.IngredientDto;
import spring.io.rest.recipes.services.dtos.mappers.IngredientMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class IngredientService {
    private final IngredientRepository ingredientRepository;
    private final IngredientMapper ingredientMapper;

    @Autowired
    public IngredientService(IngredientRepository ingredientRepository, IngredientMapper ingredientMapper) {
        this.ingredientRepository = ingredientRepository;
        this.ingredientMapper = ingredientMapper;
    }

    public void addListOfIngredients(List<IngredientDto> ingredientDtoList){
        Optional.ofNullable(ingredientDtoList).orElseThrow(() -> new ApiOperationException("empty list"));
        List<Ingredient> ingredientList= ingredientMapper.toIngredientList(ingredientDtoList);
        ingredientRepository.saveAll(ingredientList);
    }

    public List<IngredientDto> getIngredients() {
        List<Ingredient> ingredientList = ingredientRepository.findAll();
        return ingredientMapper.toIngredientDtoList(ingredientList);
    }

    public void updateIngredient(IngredientDto ingredientDto) {
        Ingredient ingredient = ingredientRepository.findById(ingredientDto.getId()).orElseThrow(() -> new ApiOperationException("No such ingredient"));
        ingredientMapper.editIngredient(ingredientDto, ingredient);
    }

    public List<IngredientDto> getAllIngredientsStartingWith(String ingredientName) {
        String formattedString = Arrays.stream(ingredientName.split(" ")).filter((str) -> !str.equals(""))
                .collect(Collectors.joining(" "));
        List<Ingredient> ingredientList = ingredientRepository.findIngredientsStartingWith(formattedString);
        return ingredientMapper.toIngredientDtoList(ingredientList);
    }
}
