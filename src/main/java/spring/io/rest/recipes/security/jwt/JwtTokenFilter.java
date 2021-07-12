package spring.io.rest.recipes.security.jwt;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import spring.io.rest.recipes.exceptions.ApiAccessException;
import spring.io.rest.recipes.exceptions.ApiOperationException;
import spring.io.rest.recipes.models.entities.User;
import spring.io.rest.recipes.security.UserPrincipal;
import spring.io.rest.recipes.services.UserService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@Getter
public class JwtTokenFilter extends OncePerRequestFilter {
    private JwtTokenUtil jwtTokenUtil;
    private UserService userService;
    private HandlerExceptionResolver exceptionHandler;

    @Autowired
    public void setExceptionHandler( @Qualifier("handlerExceptionResolver") HandlerExceptionResolver exceptionHandler) {
        this.exceptionHandler = exceptionHandler;
    }

    @Autowired
    public void setJwtTokenUtil(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        try {

            String token = jwtTokenUtil.extractToken(request);
            if (token == null) {
                chain.doFilter(request, response);
                return;
            }

            jwtTokenUtil.validate(token);

            // Get user identity and set it on the spring security context
            String payload = jwtTokenUtil.getSubjectFromToken(token);
            Long userId = Long.parseLong(payload.trim().split(":")[0]);
            String email = payload.trim().split(":")[1];

            User user = userService.getUserById(userId);

            UserDetails userDetails = UserPrincipal.create(user);

            UsernamePasswordAuthenticationToken
                    authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null,
                    userDetails.getAuthorities()
            );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(request, response);
        }
        catch(ApiOperationException ex){
            exceptionHandler.resolveException(request, response, null,
                    new ApiAccessException(ex.getMessage()));
        } catch (Exception accessException){
            exceptionHandler.resolveException(request, response, null, accessException);
        }
    }
}
