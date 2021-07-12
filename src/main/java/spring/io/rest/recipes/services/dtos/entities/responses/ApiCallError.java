package spring.io.rest.recipes.services.dtos.entities.responses;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiCallError {
    private int statusCode;
    private String URI;
    private String message;
    private LocalDateTime timestamp;
    private List<String> details;
}
