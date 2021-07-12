package spring.io.rest.recipes.controllers.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.io.rest.recipes.security.AuthRequest;
import spring.io.rest.recipes.security.AuthResponse;
import spring.io.rest.recipes.services.CustomUserDetailsService;
import spring.io.rest.recipes.services.IngredientService;
import spring.io.rest.recipes.services.UserService;
import spring.io.rest.recipes.services.dtos.entities.IngredientDto;
import spring.io.rest.recipes.services.dtos.entities.UserDto;
import spring.io.rest.recipes.services.dtos.entities.UserProxyDto;
import spring.io.rest.recipes.services.dtos.entities.UserUpdateDto;
import spring.io.rest.recipes.services.dtos.entities.responses.ApiMessageResponse;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@RestController
@RequestMapping("${app.uri.prefix}/admin")
public class AdminSpecificController {

    private final IngredientService ingredientService;
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;

    @Autowired
    public AdminSpecificController(IngredientService ingredientService, UserService userService, CustomUserDetailsService customUserDetailsService) {
        this.ingredientService = ingredientService;
        this.userService = userService;
        this.customUserDetailsService = customUserDetailsService;
    }

    @RolesAllowed("ROLE_ADMIN")
    @PostMapping("ingredients")
    public ResponseEntity<ApiMessageResponse> addIngredients(@RequestBody List<IngredientDto> ingredientDtoList) {
        ingredientService.addListOfIngredients(ingredientDtoList);
        return ResponseEntity.ok().body(ApiMessageResponse.defaultSuccessResponse());
    }

    @RolesAllowed("ROLE_ADMIN")
    @PutMapping("ingredient")
    public ResponseEntity<ApiMessageResponse> updateIngredient(@RequestBody IngredientDto ingredientDto) {
        ingredientService.updateIngredient(ingredientDto);
        return ResponseEntity.ok().body(ApiMessageResponse.defaultSuccessResponse());
    }

    @RolesAllowed("ROLE_ADMIN")
    @GetMapping("ingredients")
    public ResponseEntity<List<IngredientDto>> viewIngredients() {
        List<IngredientDto> ingredientDtoList = ingredientService.getIngredients();
        return ResponseEntity.ok().body(ingredientDtoList);
    }

    /* Admin user controllers starts here*/

    @PostMapping("user/register")
    public ResponseEntity<UserProxyDto> adminRegister(@RequestBody UserDto userDto) {
        UserProxyDto userProxyDto = userService.registerUser(userDto);
        return ResponseEntity.ok().body(userProxyDto);
    }

    @RolesAllowed("ROLE_ADMIN")
    @PutMapping("user/update")
    public ResponseEntity<ApiMessageResponse> adminUpdate(@RequestBody UserUpdateDto userUpdateDto) {
        userService.updateUser(userUpdateDto);
        return ResponseEntity.ok().body(ApiMessageResponse.defaultSuccessResponse());
    }

    @PostMapping("login")
    public ResponseEntity<UserProxyDto> login(@RequestBody AuthRequest authRequest) {
        AuthResponse authResponse = customUserDetailsService.login(authRequest);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authResponse.getJwtToken())
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "*", HttpHeaders.AUTHORIZATION)
                .body(authResponse.getUser());
    }

    /* Admin user controllers end here*/

}
