package spring.io.rest.recipes.security.oauth2.userinfo;

import java.util.Map;

public class GoogleUserData implements Oauth2UserData{
    private final Map<String, Object> attributes;

    public GoogleUserData(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
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
        return (String) attributes.get("picture");
    }
}
