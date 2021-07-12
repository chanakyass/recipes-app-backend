package spring.io.rest.recipes.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiAccessException extends RuntimeException {
    public ApiAccessException(String message) {
        super(message);
    }
}
