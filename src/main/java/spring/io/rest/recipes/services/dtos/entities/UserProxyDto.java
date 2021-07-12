package spring.io.rest.recipes.services.dtos.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserProxyDto {
    private Long id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String profileName;
    private String email;
    private String userSummary;
}
