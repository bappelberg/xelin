package se.foi.xelin.identity.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.ldap.LdapPasswordComparisonAuthenticationManagerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
public class WebSecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(BaseLdapPathContextSource contextSource) {
        LdapPasswordComparisonAuthenticationManagerFactory factory =
            new LdapPasswordComparisonAuthenticationManagerFactory(
                contextSource, new BCryptPasswordEncoder());
        factory.setUserDnPatterns("uid={0},ou=people");
        factory.setPasswordAttribute("userPassword");

        // Rollhärledning från gruppmedlemskap (KR-102): cn=User -> behörighet ROLE_User.
        // convertToUpperCase=false behåller gruppnamnet exakt så att hasRole('User') matchar.
        DefaultLdapAuthoritiesPopulator authorities =
            new DefaultLdapAuthoritiesPopulator(contextSource, "ou=groups");
        authorities.setGroupSearchFilter("(member={0})");
        authorities.setGroupRoleAttribute("cn");
        authorities.setConvertToUpperCase(false);
        factory.setLdapAuthoritiesPopulator(authorities);

        return factory.createAuthenticationManager();
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
                // --- LÄGG TILL DETTA BLOCK ---
                .sessionManagement(session -> session
                        // Skapa ALDRIG en session för anonyma rutter.
                        // Skapa endast om koden explicit ber om det (vilket din AuthController gör via repository.saveContext)
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                /*
                * Problemet ligger i att Spring Boot är för "snäll" och skapar en ny tom session åt dig så fort du
                * blivit utloggad och laddar om sidan. Genom att sätta SessionCreationPolicy.IF_REQUIRED i Java
                * tvingar du Spring Boot att hålla sig lugn tills användaren faktiskt knappar in sitt lösenord igen!
                * */

            // Ingen CORS-konfiguration: frontend/portal pratar med API:et via Next.js server-side
            // proxy (samma origin), så inga cross-origin-anrop når backend.

            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/auth/login", "/api/auth/logout").permitAll()
                .anyRequest().authenticated()
            )

            // REST-API: svara 401 på oautentiserade anrop i stället för redirect till inloggningssida.
            .exceptionHandling(ex -> ex.authenticationEntryPoint(
                (request, response, authException) ->
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED)))
            .logout(logout -> logout
            .logoutUrl("/api/auth/logout") // URL:en som frontend ska skicka sitt POST-anrop till
            .invalidateHttpSession(true)   // Dödar sessionen på servern direkt
            .clearAuthentication(true)     // Tömmer trådens säkerhetskontext (loggar ut användaren i Java)
            .deleteCookies("JSESSIONID")   // Säger till webbläsaren att radera inloggnings-cookien
            .logoutSuccessHandler((request, response, authentication) -> {
                response.setStatus(HttpServletResponse.SC_OK); // Returnerar HTTP 200 OK istället för att skicka en HTML-redirect
                })
            )

            // 2. SecurityContextRepository skapas direkt här i stället för en egen metod
            .securityContext(sc -> sc.securityContextRepository(new HttpSessionSecurityContextRepository()));

        return http.build();
    }
}