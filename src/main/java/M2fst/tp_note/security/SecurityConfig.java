package M2fst.tp_note.security;

import M2fst.tp_note.dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Configuration
@EnableMethodSecurity
public class SecurityConfig
{

    // Configuration de la sécurité pour l'application
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, ObjectMapper objectMapper) throws Exception
    {
        AccessDeniedHandler accessDeniedHandler = (request, response, exception) ->
        {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            ErrorResponseDTO error =
                    new ErrorResponseDTO(403, "Acces interdit a cette ressource.");

            response.getWriter().write(objectMapper.writeValueAsString(error));
        };

        http.oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                        .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(accessDeniedHandler));

        return http.build();
    }

    // Configuration du convertisseur d'authentification JWT pour extraire les rôles des utilisateurs à partir du token JWT
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter()
    {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Collection<GrantedAuthority> authorities = new ArrayList<>();

            Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess != null && realmAccess.containsKey("roles"))
            {
                Collection<String> roles = (Collection<String>) realmAccess.get("roles");
                for (String role : roles)
                {
                    authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                }
            }

            Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
            if (resourceAccess != null)
            {
                Map<String, Object> clientAccess = (Map<String, Object>) resourceAccess.get("coursm2");
                if (clientAccess != null && clientAccess.containsKey("roles"))
                {
                    Collection<String> roles = (Collection<String>) clientAccess.get("roles");

                    for (String role : roles)
                        authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
                }
            }

            JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();
            authorities.addAll(defaultConverter.convert(jwt));

            return authorities;
        });

        return converter;
    }
}