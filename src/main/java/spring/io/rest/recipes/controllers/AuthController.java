package spring.io.rest.recipes.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.io.rest.recipes.security.AuthRequest;
import spring.io.rest.recipes.security.AuthResponse;
import spring.io.rest.recipes.services.CustomUserDetailsService;
import spring.io.rest.recipes.services.UserService;
import spring.io.rest.recipes.services.dtos.entities.UserDto;
import spring.io.rest.recipes.services.dtos.entities.UserProxyDto;

@RestController
@RequestMapping("${app.uri.prefix}")
public class AuthController {
    private final CustomUserDetailsService customUserDetailsService;
    private final UserService userService;

    @Autowired
    public AuthController(CustomUserDetailsService customUserDetailsService, UserService userService) {
        this.customUserDetailsService = customUserDetailsService;
        this.userService = userService;
    }

    @PostMapping("public/login")
    public ResponseEntity<UserProxyDto> loginUser(@RequestBody AuthRequest authRequest){
        AuthResponse authResponse = customUserDetailsService.login(authRequest);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, authResponse.getJwtToken())
                .header(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "*", HttpHeaders.AUTHORIZATION)
                .body(authResponse.getUser());
    }

    @PostMapping("public/register")
    public ResponseEntity<UserProxyDto> register(@RequestBody UserDto userDto){
        UserProxyDto userProxyDto = userService.registerUser(userDto);
        return ResponseEntity.ok().body(userProxyDto);
    }
}
