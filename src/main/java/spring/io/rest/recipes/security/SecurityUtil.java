package spring.io.rest.recipes.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import spring.io.rest.recipes.exceptions.ApiAccessException;
import spring.io.rest.recipes.models.entities.User;
import spring.io.rest.recipes.repositories.UserRepository;

import java.util.Optional;

@Component
public class SecurityUtil {

    private final UserRepository userRepos;

    @Autowired
    public SecurityUtil(UserRepository userRepos) {
        this.userRepos = userRepos;
    }

    public UserDetails getUserService() {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public Long getSubjectId() {
        UserDetails userDetails =  getUserService();
        User user = Optional.ofNullable(userRepos.findUserByEmail(userDetails.getUsername())).orElseThrow(()->new ApiAccessException("Wrong credentials"));
        return user.getId();
    }

    public User getUserFromSubject()
    {
        UserDetails userDetails = getUserService();
        return Optional.ofNullable(userRepos.findUserByEmail(userDetails.getUsername())).orElseThrow(()->new ApiAccessException("Wrong credentials"));
    }

    public String getSubjectUsername() {
        UserDetails userDetails =  getUserService();
        return userDetails.getUsername();
    }


}
