package spring.io.rest.recipes.unittests.data;

import spring.io.rest.recipes.models.entities.FullName;
import spring.io.rest.recipes.models.entities.Role;
import spring.io.rest.recipes.models.entities.User;
import spring.io.rest.recipes.services.dtos.entities.RoleDto;
import spring.io.rest.recipes.services.dtos.entities.UserDto;
import spring.io.rest.recipes.services.dtos.entities.UserProxyDto;
import spring.io.rest.recipes.services.dtos.entities.UserUpdateDto;

import java.time.LocalDate;
import java.util.List;

public class UserTestDataFactory {

    public UserDto getRandomUserDto() {
        return new UserDto(1L, "Random", "", "Name", "random_user444", "Software Developer",
                LocalDate.of(1995, 6, 10), "random_user444@rest.com", "randompass",
                List.of(new RoleDto(1L, "ROLE_USER")));
    }

    public User getRandomUser() {
        return new User(1L, new FullName("Random", "", "Name"), "random_user444", "random_user444@rest.com", "randpmpass",
                LocalDate.of(1995, 6, 10), "Software Developer",
                List.of(new Role(1L, "ROLE_USER")));
    }

    public UserUpdateDto getRandomUserUpdateDto() {
        User user = getRandomUser();
        FullName fullName = user.getFullName();
        return new UserUpdateDto(user.getId(), fullName.getFirstName(), fullName.getMiddleName(), fullName.getLastName(),
                user.getProfileName(), user.getEmail(), user.getDob(), user.getUserSummary());
    }


    public UserProxyDto getRandomUserProxyDto() {
        return getUserProxyDto(getRandomUser());
    }


    private UserProxyDto getUserProxyDto(User user) {
        FullName fullName = user.getFullName();
        return new UserProxyDto(user.getId(), fullName.getFirstName(), fullName.getMiddleName(), fullName.getLastName(),
                user.getProfileName(), user.getEmail(), user.getUserSummary());
    }

}
