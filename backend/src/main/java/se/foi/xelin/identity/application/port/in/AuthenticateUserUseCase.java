package se.foi.xelin.identity.application.port.in;

import se.foi.xelin.identity.domain.model.AuthenticatedUser;

// Vad kan systemet göra?
public interface AuthenticateUserUseCase {
    AuthenticatedUser authenticate(String username, String password);
}
