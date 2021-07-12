package spring.io.rest.recipes.unittests.exceptions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.servlet.NoHandlerFoundException;
import spring.io.rest.recipes.exceptions.ApiAccessException;
import spring.io.rest.recipes.exceptions.ApiOperationException;
import spring.io.rest.recipes.exceptions.GlobalExceptionHandler;
import spring.io.rest.recipes.services.dtos.entities.responses.ApiCallError;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Spy
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private MockHttpServletRequest request;

    @Test
    void handleNotFoundException() {
        NoHandlerFoundException exception = Mockito.mock(NoHandlerFoundException.class);
        when(exception.getMessage()).thenReturn("RANDOM_STRING");
        ResponseEntity<ApiCallError> responseEntity = globalExceptionHandler.handleNotFoundException(request, exception);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.NOT_FOUND);
        assertEquals(Objects.requireNonNull(responseEntity.getBody()).getMessage(), HttpStatus.NOT_FOUND.getReasonPhrase());
    }

    @Test
    void handleAuthenticationException() {
        AuthenticationException exception = Mockito.mock(AuthenticationException.class);
        when(exception.getMessage()).thenReturn("RANDOM_STRING");
        ResponseEntity<ApiCallError> responseEntity = globalExceptionHandler.handleAuthenticationException(request, exception);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.UNAUTHORIZED);
        assertEquals(Objects.requireNonNull(responseEntity.getBody()).getMessage(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }

    @Test
    void handleAccessDeniedException() {
        ApiAccessException exception = Mockito.mock(ApiAccessException.class);
        when(exception.getMessage()).thenReturn("RANDOM_STRING");
        ResponseEntity<ApiCallError> responseEntity = globalExceptionHandler.handleAccessDeniedException(request, exception);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.UNAUTHORIZED);
        assertEquals(Objects.requireNonNull(responseEntity.getBody()).getMessage(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }

    @Test
    void testHandleAccessDeniedException() {
        AccessDeniedException exception = Mockito.mock(AccessDeniedException.class);
        when(exception.getMessage()).thenReturn("RANDOM_STRING");
        ResponseEntity<ApiCallError> responseEntity = globalExceptionHandler.handleAccessDeniedException(request, exception);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.FORBIDDEN);
        assertEquals(Objects.requireNonNull(responseEntity.getBody()).getMessage(), HttpStatus.FORBIDDEN.getReasonPhrase());
    }

    @Test
    void handleApiSpecificException() {
        ApiOperationException exception = Mockito.mock(ApiOperationException.class);
        when(exception.getMessage()).thenReturn("RANDOM_STRING");
        ResponseEntity<ApiCallError> responseEntity = globalExceptionHandler.handleApiSpecificException(request, exception);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        assertEquals(Objects.requireNonNull(responseEntity.getBody()).getMessage(), "Error in processing!");
    }

    @Test
    void handleInternalServerError() {
        NullPointerException exception = Mockito.mock(NullPointerException.class);
        when(exception.getMessage()).thenReturn("RANDOM_STRING");
        ResponseEntity<ApiCallError> responseEntity = globalExceptionHandler.handleInternalServerError(request, exception);
        assertEquals(responseEntity.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
        assertEquals(Objects.requireNonNull(responseEntity.getBody()).getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase());
    }
}