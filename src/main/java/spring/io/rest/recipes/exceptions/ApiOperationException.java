package spring.io.rest.recipes.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiOperationException extends RuntimeException{
    public ApiOperationException(String message) {
        super(message);
    }
}
