package spring.io.rest.recipes.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.io.rest.recipes.models.entities.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByAuthority(String authority);
}
