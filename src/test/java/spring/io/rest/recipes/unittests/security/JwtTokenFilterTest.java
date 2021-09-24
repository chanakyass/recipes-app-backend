package spring.io.rest.recipes.unittests.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerExceptionResolver;
import spring.io.rest.recipes.exceptions.ApiAccessException;
import spring.io.rest.recipes.exceptions.ApiOperationException;
import spring.io.rest.recipes.models.entities.User;
import spring.io.rest.recipes.security.jwt.JwtTokenFilter;
import spring.io.rest.recipes.security.jwt.JwtTokenUtil;
import spring.io.rest.recipes.services.UserService;
import spring.io.rest.recipes.unittests.data.AbstractTestDataFactory;
import spring.io.rest.recipes.unittests.data.UserTestDataFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtTokenFilterTest {

    @Mock
    private JwtTokenUtil jwtTokenUtil;
    @Mock
    private UserService userService;
    @Mock
    private HandlerExceptionResolver exceptionHandler;

    @InjectMocks
    private JwtTokenFilter jwtTokenFilter;

    private MockFilterChain mockChain;
    private MockHttpServletRequest request;
    private HttpServletResponse response;
    private String token;
    private String payload;
    private User user;

    @BeforeEach
    void setUp() throws ServletException, IOException {
        UserTestDataFactory userTestDataFactory = AbstractTestDataFactory.getUserTestDataFactory();
        user = userTestDataFactory.getRandomUser();
        mockChain =  Mockito.mock(MockFilterChain.class);
        request = Mockito.mock(MockHttpServletRequest.class);
        response = Mockito.mock(MockHttpServletResponse.class);

        Random random = new Random();

        int leftLimit = 97, rightLimit = 122;

        token = random.ints(leftLimit, rightLimit + 1)
                .limit(12)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        payload = random.ints(leftLimit, rightLimit + 1)
                .limit(4)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();

        when(jwtTokenUtil.extractToken(any(MockHttpServletRequest.class))).thenReturn(token);

    }

    @Test
    void doFilterInternalFail_whenUserNotAvailable() throws ServletException, IOException {
        when(userService.getUserByEmail(anyString())).thenThrow(ApiOperationException.class);
        when(jwtTokenUtil.getSubjectFromToken(anyString())).thenReturn(1L +":"+payload+":"+payload);
        jwtTokenFilter.doFilter(request, response, mockChain);
        verify(exceptionHandler, times(1)).resolveException(any(MockHttpServletRequest.class), any(MockHttpServletResponse.class),
                any(), any(Exception.class));
    }

    @Test
    void doFilterInternalFail_whenTokenNotValid() throws ServletException, IOException {
        doThrow(ApiAccessException.class).when(jwtTokenUtil).validate(anyString());
        jwtTokenFilter.doFilter(request, response, mockChain);
        verify(exceptionHandler, times(1)).resolveException(any(MockHttpServletRequest.class), any(MockHttpServletResponse.class),
                any(), any(Exception.class));
    }


}