package spring.io.rest.recipes.security.oauth2.userinfo;

import java.util.Map;

public class GithubUserData implements Oauth2UserData {

    private final Map<String, Object> attributes;

    public GithubUserData(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getId() {
        return ((Integer) attributes.get("id")).toString();
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("avatar_url");
    }
}
