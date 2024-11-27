package es.ujaen.dae.rest;

import es.ujaen.dae.excepciones.UsuarioYaRegistrado;
import es.ujaen.dae.rest.dto.DSocio;
import es.ujaen.dae.rest.dto.Mapeador;
import es.ujaen.dae.servicios.ServicioSocios;
import es.ujaen.dae.servicios.ServiciosAdmin;
import jakarta.validation.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}