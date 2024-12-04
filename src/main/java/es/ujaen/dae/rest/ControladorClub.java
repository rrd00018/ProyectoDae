package es.ujaen.dae.rest;

import es.ujaen.dae.entidades.Socio;
import es.ujaen.dae.entidades.Solicitud;
import es.ujaen.dae.entidades.Socio;
import es.ujaen.dae.excepciones.ActividadNoExistente;
import es.ujaen.dae.excepciones.ActividadYaCreada;
import es.ujaen.dae.excepciones.TemporadaYaCreada;
import es.ujaen.dae.excepciones.UsuarioYaRegistrado;
import es.ujaen.dae.rest.dto.DSocio;
import es.ujaen.dae.rest.dto.DSolicitud;
import es.ujaen.dae.rest.dto.Mapeador;
import es.ujaen.dae.rest.dto.*;
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
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/actividades")
    public ResponseEntity<Void> nuevaActividad(@RequestBody DActividad actividad){
        try{
            serviciosAdmin.crearActividad(actividad.titulo(), actividad.descripcion(), actividad.precio(), actividad.id(), actividad.fechaCelebracion(), actividad.fechaInicioInscripcion(), actividad.fechaFinInscripcion());
        }catch(ActividadYaCreada u){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/temporadas")
    public ResponseEntity<Void> nuevaTemporada(){
        try{
            serviciosAdmin.crearTemporada();
        }catch(TemporadaYaCreada u){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    // Rutas para actividades
    @GetMapping("/actividades/{idActividad}")
    public ResponseEntity<DActividad> obtenerActividad(@PathVariable int idActividad) {
        try {
            serviciosAdmin.buscarActividad(idActividad);
        } catch (ActividadNoExistente u) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
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

    // Rutas para solicitudes
    @GetMapping("/solicitudes/{idSolicitud}")
    public ResponseEntity<DSolicitud> obtenerSolicitud(@PathVariable int idSolicitud) {
        try {
        } catch () {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    // Rutas para solicitudes
    @GetMapping("/temporada/{anio}")
    public ResponseEntity<DSolicitud> obtenerTemporada(@PathVariable int anio) {
        try {
        } catch () {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
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