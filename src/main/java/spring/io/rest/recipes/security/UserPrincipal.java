package spring.io.rest.recipes.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import spring.io.rest.recipes.models.entities.FullName;
import spring.io.rest.recipes.models.entities.User;

import java.util.Collection;

@Data
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private final User user;

    public static UserPrincipal create(User user){
        return new UserPrincipal(user);
    }

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
}