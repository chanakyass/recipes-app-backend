package spring.io.rest.recipes.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties("app.oauth2")
@Getter
@Setter
public class OAuth2SecurityProperties {

    private List<String> authorizedRedirectUri;
}
