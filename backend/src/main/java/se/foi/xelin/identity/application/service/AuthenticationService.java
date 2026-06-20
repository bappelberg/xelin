package se.foi.xelin.identity.application.service;

import org.springframework.stereotype.Service;
import se.foi.xelin.identity.domain.model.AuthenticatedUser;
import se.foi.xelin.identity.application.port.in.AuthenticateUserUseCase;
import se.foi.xelin.identity.application.port.out.IdentityProvider;

@Service
public class AuthenticationService implements AuthenticateUserUseCase {

    private final IdentityProvider identityProvider;

    public AuthenticationService(IdentityProvider identityProvider) {
        this.identityProvider = identityProvider;
    }

    @Override
    public AuthenticatedUser authenticate(String username, String password) {
        return identityProvider.authenticate(username, password);
    }
}
