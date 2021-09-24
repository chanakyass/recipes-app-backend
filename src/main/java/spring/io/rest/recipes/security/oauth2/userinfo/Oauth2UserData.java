package spring.io.rest.recipes.security.oauth2.userinfo;

public interface Oauth2UserData {
    public String getId();

    public String getName();

    public String getEmail();

    public String getImageUrl();
}
