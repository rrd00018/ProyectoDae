package es.ujaen.dae.seguridad;

import es.ujaen.dae.entidades.Socio;
import es.ujaen.dae.servicios.ServiciosAdmin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ServicioCredenciales implements UserDetailsService {
    @Autowired
    ServiciosAdmin serviciosAdmin;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        Socio usuario = serviciosAdmin.recuperarSocioPorEmail(userName).orElseThrow(() -> new UsernameNotFoundException(""));

        return User.withUsername(usuario.getEmail())
                .password(usuario.getClaveAcceso())
                .roles(usuario.getNombre().equals("admin") ? "DIRECCION": "CLIENTE")
                .build();
    }

}
