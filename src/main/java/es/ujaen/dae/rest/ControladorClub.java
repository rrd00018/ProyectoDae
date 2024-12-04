package es.ujaen.dae.rest;

import es.ujaen.dae.entidades.Socio;
import es.ujaen.dae.entidades.Solicitud;
import es.ujaen.dae.excepciones.UsuarioYaRegistrado;
import es.ujaen.dae.rest.dto.DSocio;
import es.ujaen.dae.rest.dto.DSolicitud;
import es.ujaen.dae.rest.dto.Mapeador;
import es.ujaen.dae.servicios.ServicioSocios;
import es.ujaen.dae.servicios.ServiciosAdmin;
import es.ujaen.dae.excepciones.UsuarioNoRegistrado;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/clubDeSocios")
public class ControladorClub {
    @Autowired
    Mapeador mapeador;

    @Autowired
    ServiciosAdmin serviciosAdmin;

    @Autowired
    ServicioSocios servicioSocios;

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(ConstraintViolationException.class)
    public void mapeadoExcepcionConstraintViolationException(){}

    @PostMapping("/socios")
    public ResponseEntity<Void> nuevoSocio(@RequestBody DSocio socio){
        try{
            serviciosAdmin.crearSocio(socio.email(),socio.nombre(),socio.apellidos(),socio.telefono(),socio.claveAcceso());
        }catch(UsuarioYaRegistrado u){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/socios/{email}")
    public ResponseEntity<DSocio> loginSocio(@PathVariable String email, @RequestParam String password){
        try {
            Socio socio = serviciosAdmin.login(email, password).orElseThrow(UsuarioNoRegistrado::new);
            return ResponseEntity.ok(mapeador.dto(socio));
        }
        catch(UsuarioNoRegistrado e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/socios/{email}/solicitudes")
    public ResponseEntity<List<DSolicitud>> solicitudesSocio(@PathVariable String email){
        List<Solicitud> solicitudes;
        try {
            Socio socio = serviciosAdmin.recuperarSocioPorEmail(email);

            solicitudes = servicioSocios.obtenerSolicitudes(socio);

            return ResponseEntity.ok(solicitudes.stream().map(s -> mapeador.dto(s)).toList());
        }catch(UsuarioNoRegistrado u){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
}