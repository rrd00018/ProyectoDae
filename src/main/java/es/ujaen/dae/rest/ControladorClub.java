package es.ujaen.dae.rest;

import es.ujaen.dae.entidades.*;
import es.ujaen.dae.entidades.Socio;
import es.ujaen.dae.excepciones.*;
import es.ujaen.dae.rest.dto.DSocio;
import es.ujaen.dae.rest.dto.DSolicitud;
import es.ujaen.dae.rest.dto.Mapeador;
import es.ujaen.dae.rest.dto.*;
import es.ujaen.dae.servicios.ServicioSocios;
import es.ujaen.dae.servicios.ServiciosAdmin;
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

    @GetMapping("/temporada/{anio}")
    public ResponseEntity<DTemporada> obtenerTemporada(@PathVariable int anio) {
        try {
            Temporada t = serviciosAdmin.buscarTemporada(anio);
            return ResponseEntity.ok(mapeador.dto(t));
        } catch (TemporadaNoExiste e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
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

    @GetMapping("/temporadas/{anio}/actividades")
    public ResponseEntity<List<DActividad>> obtenerActividadesTemporada(@PathVariable int anio, @RequestParam(defaultValue = "0") boolean enCurso) {
        try{
            List<Actividad> actividades;
            Temporada t = serviciosAdmin.buscarTemporada(anio);
            actividades = t.listarActividades(enCurso);
            return ResponseEntity.ok(actividades.stream().map(a -> mapeador.dto(a)).toList());
        }catch(TemporadaNoExiste e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/temporadas")
    public ResponseEntity<List<DTemporada>> obtenerTodasLasTemporadas(){
        return ResponseEntity.ok(serviciosAdmin.getTemporadas().stream().map(a -> mapeador.dto(a)).toList());
    }

    @GetMapping("/actividades/{idActividad}")
    public ResponseEntity<DActividad> obtenerActividad(@PathVariable int idActividad) {
        try {
            serviciosAdmin.buscarActividad(idActividad);
        } catch (ActividadNoExistente u) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/actividades")
    public ResponseEntity<Void> nuevaActividad(@RequestBody DActividad actividad){
        try{
            serviciosAdmin.crearActividad(actividad.titulo(), actividad.descripcion(), actividad.precio(), actividad.plazas(), actividad.fechaCelebracion(), actividad.fechaInicioInscripcion(), actividad.fechaFinInscripcion());
        }catch(ActividadYaCreada u){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/actividades/{idActividad}/solicitudes")
    public ResponseEntity<List<DSolicitud>> obtenerSolicitudes(@PathVariable int idActividad) {
        try{
            List<Solicitud> solicitudes;
            Actividad a = serviciosAdmin.buscarActividad(idActividad);
            solicitudes = a.getSolicitudes();
            return ResponseEntity.ok(solicitudes.stream().map(s -> mapeador.dto(s)).toList());
        }catch(ActividadYaCreada u){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
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

    @PostMapping("/socios")
    public ResponseEntity<Void> nuevoSocio(@RequestBody DSocio socio){
        try{
            serviciosAdmin.crearSocio(socio.email(),socio.nombre(),socio.apellidos(),socio.telefono(),socio.claveAcceso());
        }catch(UsuarioYaRegistrado u){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
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

    // Rutas para solicitudes
    @GetMapping("/solicitudes/{idSolicitud}")
    public ResponseEntity<DSolicitud> obtenerSolicitud(@PathVariable int idSolicitud) {
        try {
            Solicitud s = serviciosAdmin.buscarSolicitud(idSolicitud);
            return ResponseEntity.ok(mapeador.dto(s));
        } catch (SolicitudIncorrecta e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }



}