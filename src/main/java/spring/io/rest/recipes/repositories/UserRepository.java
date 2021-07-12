package spring.io.rest.recipes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import spring.io.rest.recipes.models.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select distinct usr from User usr join fetch usr.grantedAuthoritiesList where usr.email=:email")
    User findUserByEmail(String email);

    @Override
    @Query("select distinct usr from User usr join fetch usr.grantedAuthoritiesList where usr.id=:id")
    Optional<User> findById(Long id);
}
