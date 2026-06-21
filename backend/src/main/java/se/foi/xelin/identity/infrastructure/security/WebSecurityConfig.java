package se.foi.xelin.identity.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.support.BaseLdapPathContextSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.ldap.LdapPasswordComparisonAuthenticationManagerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.cors.CorsConfiguration;

import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

@Configuration
public class WebSecurityConfig {

    @Bean
    public AuthenticationManager authenticationManager(BaseLdapPathContextSource contextSource) {
        LdapPasswordComparisonAuthenticationManagerFactory factory =
            new LdapPasswordComparisonAuthenticationManagerFactory(
                contextSource, new BCryptPasswordEncoder());
        factory.setUserDnPatterns("uid={0},ou=people");
        factory.setPasswordAttribute("userPassword");
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

            // 1. CORS konfigureras direkt här via en lambda-funktion
            .cors(cors -> cors.configurationSource(request -> {
                CorsConfiguration config = new CorsConfiguration();
                config.setAllowedOrigins(List.of("http://localhost:3000"));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                config.setAllowedHeaders(List.of("*"));
                config.setAllowCredentials(true);
                return config;
            }))

            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/api/auth/login", "/api/auth/logout").permitAll()
                .anyRequest().authenticated()
            )
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