package spring.io.rest.recipes.exceptions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;
import spring.io.rest.recipes.services.dtos.entities.responses.ApiCallError;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final Logger logger = LogManager.getLogger();

    public GlobalExceptionHandler() {
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiCallError> handleNotFoundException(HttpServletRequest request, NoHandlerFoundException ex) {
        logger.error("NotFoundException {}\n", request.getRequestURI(), ex);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ApiCallError(HttpStatus.NOT_FOUND.value(),request.getRequestURI(),
                        HttpStatus.NOT_FOUND.getReasonPhrase(), LocalDateTime.now(), List.of(ex.getMessage())));

    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<ApiCallError> handleAuthenticationException(HttpServletRequest request, AuthenticationException ex) {
        logger.error("handleAuthenticationException {}\n", request.getRequestURI(), ex);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ApiCallError(HttpStatus.UNAUTHORIZED.value(), request.getRequestURI(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(), LocalDateTime.now(), List.of(ex.getMessage() )));
    }

    @ExceptionHandler({ ApiAccessException.class})
    public ResponseEntity<ApiCallError> handleAccessDeniedException(HttpServletRequest request, ApiAccessException ex)   {
        logger.error("handleAccessDeniedException {}\n", request.getRequestURI(), ex);

        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body((new ApiCallError(HttpStatus.UNAUTHORIZED.value(), request.getRequestURI(),
                        HttpStatus.UNAUTHORIZED.getReasonPhrase(), LocalDateTime.now(), List.of(ex.getMessage()))));

    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiCallError> handleAccessDeniedException(HttpServletRequest request, AccessDeniedException ex){
        logger.error("handleAccessDeniedException {}\n", request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiCallError(HttpStatus.FORBIDDEN.value(), request.getRequestURI(),
                        HttpStatus.FORBIDDEN.getReasonPhrase(), LocalDateTime.now(), List.of(ex.getMessage())));

    }

    @ExceptionHandler(ApiOperationException.class)
    public ResponseEntity<ApiCallError> handleApiSpecificException(HttpServletRequest request, ApiOperationException ex) {
        logger.error("handleApiSpecificException {}\n", request.getRequestURI(), ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiCallError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        request.getRequestURI(), "Error in processing!", LocalDateTime.now(), List.of(ex.getMessage())));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiCallError> handleInternalServerError(HttpServletRequest request, Exception ex) {
        logger.error("handleInternalServerError {}\n", request.getRequestURI(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiCallError(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        request.getRequestURI(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                        LocalDateTime.now(), List.of(ex.getMessage())));
    }
}
