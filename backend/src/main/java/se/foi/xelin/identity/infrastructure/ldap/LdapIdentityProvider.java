package se.foi.xelin.identity.infrastructure.ldap;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import se.foi.xelin.identity.domain.model.AuthenticatedUser;
import se.foi.xelin.identity.application.port.out.IdentityProvider;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LdapIdentityProvider implements IdentityProvider {

    private final AuthenticationManager authenticationManager;

    public LdapIdentityProvider(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    public AuthenticatedUser authenticate(String username, String password) {
        Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(username, password)
        );
        Set<String> roles = auth.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());
        return new AuthenticatedUser(auth.getName(), roles);
    }
}
