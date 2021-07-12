package spring.io.rest.recipes.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import spring.io.rest.recipes.models.entities.User;
import spring.io.rest.recipes.services.dtos.entities.UserProxyDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private UserProxyDto user;
    private String jwtToken;

    public static AuthResponse mapUserToAuthResponse(User user, String jwtToken) {
        return new AuthResponse(new UserProxyDto(user.getId(), user.getFullName().getFirstName(), user.getFullName().getMiddleName(), user.getFullName().getLastName(), user.getProfileName(), user.getEmail(), user.getUserSummary()), jwtToken);
    }
}
