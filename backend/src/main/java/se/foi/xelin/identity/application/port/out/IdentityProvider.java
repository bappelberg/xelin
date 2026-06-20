package se.foi.xelin.identity.application.port.out;

import se.foi.xelin.identity.domain.model.AuthenticatedUser;

// Vad behöver systemet från omvärlden?
public interface IdentityProvider {
    AuthenticatedUser authenticate(String username, String password);
}
