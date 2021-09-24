package spring.io.rest.recipes.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayloadDetails {
    private String email;
    private String profileName;

    public String getUsername() {
        return email;
    }

    public static PayloadDetails createPayloadDetails(UserPrincipal userPrincipal) {
        return new PayloadDetails(userPrincipal.getUsername(), userPrincipal.getProfileName());
    }
}
