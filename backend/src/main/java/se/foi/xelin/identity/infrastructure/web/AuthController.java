package se.foi.xelin.identity.infrastructure.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import se.foi.xelin.identity.domain.model.AuthenticatedUser;
import se.foi.xelin.identity.application.port.in.AuthenticateUserUseCase;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticateUserUseCase authenticateUserUseCase;
    private final HttpSessionSecurityContextRepository securityContextRepository =
        new HttpSessionSecurityContextRepository();

    public AuthController(AuthenticateUserUseCase authenticateUserUseCase) {
        this.authenticateUserUseCase = authenticateUserUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
        @RequestBody LoginRequest loginRequest,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        try {
            AuthenticatedUser user = authenticateUserUseCase.authenticate(
                loginRequest.getUsername(), loginRequest.getPassword()
            );
            var authorities = user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
            var token = UsernamePasswordAuthenticationToken.authenticated(
                user.getUsername(), null, authorities
            );
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(token);
            SecurityContextHolder.setContext(context);
            securityContextRepository.saveContext(context, request, response);
            return ResponseEntity.ok(user.getUsername());
        } catch (Exception e) {
            return ResponseEntity.status(401).body("Fel användarnamn eller lösenord");
        }
    }
}
