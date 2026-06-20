package se.foi.xelin.identity.domain.model;

import java.util.Set;

public class AuthenticatedUser {

    private final String username;
    private final Set<String> roles;

    public AuthenticatedUser(String username, Set<String> roles) {
        this.username = username;
        this.roles = roles;
    }

    public String getUsername() {
        return username;
    }

    public Set<String> getRoles() {
        return roles;
    }
}
