package se.foi.xelin.shared.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

// Aktiverar @PreAuthorize för behörighetskontroll per anrop (KR-804).
// Egen konfig — appövergripande och separat testbar från den LDAP-tunga WebSecurityConfig.
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {
}
