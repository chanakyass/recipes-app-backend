package spring.io.rest.recipes.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import spring.io.rest.recipes.models.entities.Recipe;
import spring.io.rest.recipes.models.entities.User;
import spring.io.rest.recipes.repositories.RecipeRepository;
import spring.io.rest.recipes.repositories.UserRepository;
import spring.io.rest.recipes.services.dtos.entities.RecipeDto;
import spring.io.rest.recipes.services.dtos.entities.UserUpdateDto;

import java.io.Serializable;

@Component("permissionEvaluator")
public class PermissionEvaluatorImpl implements PermissionEvaluator {

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    @Autowired
    public PermissionEvaluatorImpl(UserRepository userRepository, RecipeRepository recipeRepository) {
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;

    }

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permissionObject) {
        if(authentication != null) {
            UserDetails loggedUserDetails = (UserDetails) authentication.getPrincipal();

            if (loggedUserDetails == null || targetDomainObject == null)
                throw new AccessDeniedException("Access is denied");



            if(targetDomainObject instanceof RecipeDto) {
                RecipeDto recipeDtoBeingAffected = (RecipeDto) targetDomainObject;

                Recipe recipeBeingAffected = recipeRepository.findById(recipeDtoBeingAffected.getId())
                        .orElseThrow(() -> new AccessDeniedException("Access is denied"));

                User userAssertedInDto = userRepository.findById(recipeDtoBeingAffected.getUser().getId())
                        .orElseThrow(() -> new AccessDeniedException("Access is denied"));

                if(!loggedUserDetails.getUsername().equals(userAssertedInDto.getEmail())
                        || !recipeBeingAffected.getUser().getEmail().equals(userAssertedInDto.getEmail())) {
                    throw new AccessDeniedException("Access is denied");
                }
                else return true;

            }


            else if(targetDomainObject instanceof UserUpdateDto) {
                UserUpdateDto userBeingAffectedDto = (UserUpdateDto) targetDomainObject;

                User userBeingAffected = userRepository.findById(userBeingAffectedDto.getId())
                        .orElseThrow(() -> new AccessDeniedException("Access is denied"));

                if(!userBeingAffected.getEmail().equals(loggedUserDetails.getUsername())) {
                    throw new AccessDeniedException("Access is denied");
                }
                else return true;
            }


            else {
                throw new AccessDeniedException("Access is denied");
            }
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        if(authentication != null) {
            UserDetails loggedInUserDetails = (UserDetails) authentication.getPrincipal();
            Long resourceId = (Long) targetId;
            if(loggedInUserDetails == null || resourceId == null) {
                throw new AccessDeniedException("Access is denied");
            }



            if (targetType.equals("Recipe")) {
                Recipe recipeBeingAffected = recipeRepository.findById(resourceId)
                        .orElseThrow(() -> new AccessDeniedException("Access is denied"));

                if(!recipeBeingAffected.getUser().getEmail().equals(loggedInUserDetails.getUsername())) {
                    throw new AccessDeniedException("Access is denied");
                }
                else {
                    return true;
                }
            }

            else if(targetType.equals("User")) {
                User userBeingModified = userRepository.findById(resourceId)
                        .orElseThrow(() -> new AccessDeniedException("Access is denied"));

                if(!userBeingModified.getEmail().equals(loggedInUserDetails.getUsername())) {
                    throw new AccessDeniedException("Access is denied");
                }
                else return true;
            }

        }
        return false;
    }

}

