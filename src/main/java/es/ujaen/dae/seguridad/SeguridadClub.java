package es.ujaen.dae.seguridad;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;

@Configuration
public class SeguridadClub {
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.disable())
                .httpBasic(httpBasic -> httpBasic.realmName("clubDeSocios"))
                .authorizeHttpRequests(request -> request
                        .requestMatchers(HttpMethod.POST, "clubDeSocios/temporadas").hasRole("DIRECCION")
                        .requestMatchers(HttpMethod.POST, "clubDeSocios/actividades").hasRole("DIRECCION")
                        .requestMatchers(HttpMethod.POST, "clubDeSocios/actividades/{idActividad}").hasRole("DIRECCION")
                        .requestMatchers(HttpMethod.GET, "/clubDeSocios/socios/{email}")
                        .access(new WebExpressionAuthorizationManager("hasRole('DIRECCION') or (hasRole('CLIENTE') and #email == principal.username)"))
                        .requestMatchers(HttpMethod.GET,"/clubDeSocios/solicitudes/{idSolicitud}")
                        .access(new WebExpressionAuthorizationManager("hasRole('DIRECCION') or (hasRole('CLIENTE'))"))
                        .requestMatchers(HttpMethod.POST,"/clubDeSocios/solicitudes")
                        .access(new WebExpressionAuthorizationManager("hasRole('DIRECCION') or (hasRole('CLIENTE'))"))
                        .requestMatchers(HttpMethod.PUT,"/clubDeSocios/solicitudes")
                        .access(new WebExpressionAuthorizationManager("hasRole('DIRECCION') or (hasRole('CLIENTE'))"))
                        .requestMatchers(HttpMethod.DELETE,"/clubDeSocios/solicitudes")
                        .access(new WebExpressionAuthorizationManager("hasRole('DIRECCION') or (hasRole('CLIENTE'))"))
                        .requestMatchers(HttpMethod.GET,"/clubDeSocios/actividades/{idActividad}/solicitudes").hasRole("DIRECCION")
                        .requestMatchers(HttpMethod.GET,"/clubDeSocios/socios/{email}/solicitudes")
                        .access(new WebExpressionAuthorizationManager("hasRole('DIRECCION') or (hasRole('CLIENTE'))"))
                        .requestMatchers("/clubDeSocios/**").permitAll()
                )
                .build();
    }
}
