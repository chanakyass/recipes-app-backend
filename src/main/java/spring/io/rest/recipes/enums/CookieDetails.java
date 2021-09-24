package spring.io.rest.recipes.enums;

public enum CookieDetails {
    OAUTH2_AUTH_REQUEST("oauth2_auth_request"), OAUTH2_REDIRECT_URI("redirect_uri");
    private final String cookieName;
    CookieDetails(String cookieName) {
        this.cookieName = cookieName;
    }

    public String getCookieDetail() {
        return cookieName;
    }
}
