package spring.io.rest.recipes.unittests.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import spring.io.rest.recipes.exceptions.ApiOperationException;
import spring.io.rest.recipes.models.entities.Ingredient;
import spring.io.rest.recipes.repositories.IngredientRepository;
import spring.io.rest.recipes.services.IngredientService;
import spring.io.rest.recipes.services.dtos.entities.IngredientDto;
import spring.io.rest.recipes.services.dtos.mappers.IngredientMapper;
import spring.io.rest.recipes.unittests.data.AbstractTestDataFactory;
import spring.io.rest.recipes.unittests.data.IngredientTestDataFactory;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientServiceTest {

    @Mock
    private IngredientRepository ingredientRepository;

    @Mock
    private IngredientMapper ingredientMapper;

    @InjectMocks
    private IngredientService ingredientService;

    private List<IngredientDto> ingredientDtoList;
    private List<Ingredient> ingredientList;
    private Ingredient ingredient;
    private IngredientDto updatedIngredientDto;

    @BeforeEach
    void setUp() {
        IngredientTestDataFactory ingredientTestDataFactory = AbstractTestDataFactory.getIngredientTestDataFactory();
        ingredientDtoList = ingredientTestDataFactory.getRandomListOfIngredientDto();
        ingredientList = ingredientTestDataFactory.getRandomListOfIngredients();
        ingredient = ingredientTestDataFactory.getRandomIngredient(1L);
        updatedIngredientDto = ingredientTestDataFactory.getUpdatedIngredientDto(1L);
    }

    @Test
    void addListOfIngredients() {
        when(ingredientMapper.toIngredientList(ingredientDtoList)).thenReturn(ingredientList);
        ingredientService.addListOfIngredients(ingredientDtoList);
        verify(ingredientRepository, times(1)).saveAll(any());
    }

    @Test
    void getIngredients() {
        when(ingredientRepository.findAll()).thenReturn(ingredientList);
        when(ingredientMapper.toIngredientDtoList(ingredientList)).thenReturn(ingredientDtoList);
        assertEquals(ingredientService.getIngredients(), ingredientDtoList);
    }

    @Test
    void updateIngredientPass() {
        when(ingredientRepository.findById(anyLong())).thenReturn(Optional.ofNullable(ingredient));
        ingredientService.updateIngredient(updatedIngredientDto);
        verify(ingredientMapper, times(1)).editIngredient(any(), any());
    }

    @Test
    void updateIngredientFail() {
        when(ingredientRepository.findById(anyLong())).thenReturn(Optional.empty());
        ApiOperationException exception = assertThrows(ApiOperationException.class, () -> {
            ingredientService.updateIngredient(updatedIngredientDto);
        });
        assertEquals(exception.getMessage(), "No such ingredient");
    }

    @Test
    void getAllIngredientsStartingWith() {
        when(ingredientRepository.findIngredientsStartingWith(anyString())).thenReturn(ingredientList);
        when(ingredientMapper.toIngredientDtoList(anyList())).thenReturn(ingredientDtoList);
        assertEquals(ingredientService.getAllIngredientsStartingWith("random string"), ingredientDtoList);
    }
}