package spring.io.rest.recipes.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import spring.io.rest.recipes.models.entities.FullName;
import spring.io.rest.recipes.models.entities.User;

import java.util.Collection;
import java.util.Map;

@Data
@AllArgsConstructor
public class UserPrincipal implements UserDetails, OAuth2User {

    private final User user;
    private Map<String, Object> attributes;
    private OidcIdToken idToken;
    private OidcUserInfo userInfo;

    public UserPrincipal(User user) {
        this.user = user;
    }

    public static UserPrincipal create(User user){
        return new UserPrincipal(user);
    }


    public static UserPrincipal create(User user, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = UserPrincipal.create(user);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }

//    public static UserPrincipal create(User user, OidcIdToken oidcIdToken, OidcUserInfo oidcUserInfo) {
//        UserPrincipal userPrincipal = UserPrincipal.create(user);
//        userPrincipal.setIdToken(oidcIdToken);
//        userPrincipal.setUserInfo(oidcUserInfo);
//        return userPrincipal;
//    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getGrantedAuthoritiesList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    public Long getUserUniqueId() {
        return user.getId();
    }

    public FullName getUserFullName(){
        return user.getFullName();
    }

    public String getProfileName(){
        return user.getProfileName();
    }

    @Override
    public String getName() {
        FullName fullName = getUserFullName();
        return fullName.getFirstName() + " " + fullName.getMiddleName() + " " + fullName.getLastName();
    }
//
//    @Override
//    public Map<String, Object> getClaims() {
//        return getAttributes();
//    }
//
//    @Override
//    public OidcUserInfo getUserInfo() {
//        return userInfo;
//    }
//
//    @Override
//    public OidcIdToken getIdToken() {
//        return idToken;
//    }
}