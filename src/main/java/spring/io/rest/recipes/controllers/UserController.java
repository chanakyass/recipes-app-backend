package spring.io.rest.recipes.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import spring.io.rest.recipes.services.UserService;
import spring.io.rest.recipes.services.dtos.entities.UserProxyDto;
import spring.io.rest.recipes.services.dtos.entities.UserUpdateDto;
import spring.io.rest.recipes.services.dtos.entities.responses.ApiMessageResponse;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("${app.uri.prefix}")
@RolesAllowed({"ROLE_USER", "ROLE_ADMIN"})
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping("user")
    public ResponseEntity<ApiMessageResponse> update(@RequestBody UserUpdateDto userUpdateDto){
        userService.updateUser(userUpdateDto);
        return ResponseEntity.ok().body(ApiMessageResponse.defaultSuccessResponse());
    }

    @GetMapping("user/{userId}")
    public ResponseEntity<UserProxyDto> getUser(@PathVariable("userId") Long userId){
        UserProxyDto userProxyDto = userService.getUserProxyById(userId);
        return ResponseEntity.ok().body(userProxyDto);
    }
}
