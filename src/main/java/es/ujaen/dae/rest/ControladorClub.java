package es.ujaen.dae.rest;

import es.ujaen.dae.entidades.*;
import es.ujaen.dae.entidades.Socio;
import es.ujaen.dae.excepciones.*;
import es.ujaen.dae.rest.dto.DSocio;
import es.ujaen.dae.rest.dto.DSolicitud;
import es.ujaen.dae.rest.dto.Mapeador;
import es.ujaen.dae.rest.dto.*;
import es.ujaen.dae.seguridad.ServicioCredenciales;
import es.ujaen.dae.servicios.ServicioSocios;
import es.ujaen.dae.servicios.ServiciosAdmin;
import jakarta.validation.ConstraintViolationException;
import org.aspectj.apache.bcel.generic.RET;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
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
    @Autowired
    private ServicioCredenciales servicioCredenciales;

    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    @ExceptionHandler(ConstraintViolationException.class)
    public void mapeadoExcepcionConstraintViolationException(){}

    @GetMapping("/temporadas/{anio}")
    public ResponseEntity<DTemporada> obtenerTemporada(@PathVariable int anio) {
        try {
            Temporada t = serviciosAdmin.buscarTemporada(anio);
            return ResponseEntity.ok(mapeador.dto(t));
        } catch (TemporadaNoExiste e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/temporadas")
    public ResponseEntity<Void> nuevaTemporada(@RequestBody DTemporada temporada){
        try{
            if(temporada.anio() != LocalDate.now().getYear()){
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            serviciosAdmin.crearTemporada();
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch(TemporadaYaCreada u){
            System.out.println(temporada.anio());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
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
            Actividad a = serviciosAdmin.buscarActividad(idActividad);
            return ResponseEntity.ok(mapeador.dto(a));
        } catch (ActividadNoExistente u) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping("/actividades/{idActividad}")
    public ResponseEntity<DActividad> actualizarActividad(@RequestBody DActividad actividad, @PathVariable int idActividad   ) {
        try {
            Actividad a = mapeador.entidad(actividad);
            a.setId(idActividad);
            serviciosAdmin.actualizarActividad(a);
        }catch(ActividadNoExistente e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @PostMapping("/actividades")
    public ResponseEntity<Void> nuevaActividad(@RequestBody DActividad actividad){
        try{
            serviciosAdmin.crearActividad(actividad.titulo(), actividad.descripcion(), actividad.precio(), actividad.plazas(), actividad.fechaCelebracion(), actividad.fechaInicioInscripcion(), actividad.fechaFinInscripcion());
        }catch(FechaIncorrecta e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
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
    public ResponseEntity<DSocio> loginSocio(@PathVariable String email){
        try {
            Socio socio = serviciosAdmin.recuperarSocioPorEmail(email).orElseThrow(UsuarioNoRegistrado::new);
            return ResponseEntity.ok(mapeador.dto(socio));
        }
        catch(UsuarioNoRegistrado e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @PostMapping("/socios")
    public ResponseEntity<Void> nuevoSocio(@RequestBody DSocio socio){
        try{
            serviciosAdmin.crearSocio(mapeador.nuevaEntidad(socio));
        }catch(UsuarioYaRegistrado u){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }


    @GetMapping("/socios/{email}/solicitudes")
    public ResponseEntity<List<DSolicitud>> solicitudesSocio(@PathVariable String email){
        List<Solicitud> solicitudes;
        try {
            Socio socio = serviciosAdmin.recuperarSocioPorEmail(email).orElseThrow(UsuarioNoRegistrado::new);

            solicitudes = servicioSocios.obtenerSolicitudes(socio);

            return ResponseEntity.ok(solicitudes.stream().map(s -> mapeador.dto(s)).toList());
        }catch(UsuarioNoRegistrado u){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @GetMapping("/solicitudes/{idSolicitud}")
    public ResponseEntity<DSolicitud> obtenerSolicitud(@PathVariable int idSolicitud) {
        try {
            Solicitud s = serviciosAdmin.buscarSolicitud(idSolicitud);
            return ResponseEntity.ok(mapeador.dto(s));
        } catch (SolicitudIncorrecta e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @PostMapping("/solicitudes")
    public ResponseEntity<Void> nuevaSolicitud(@RequestBody DSolicitud dSolicitud) {
        try {
            Solicitud solicitud = servicioSocios.echarSolicitud(
                    serviciosAdmin.recuperarSocioPorId(dSolicitud.idSocio()),
                    dSolicitud.idActividad(),
                    dSolicitud.numAcompaniantes()
            );

            return ResponseEntity.status(HttpStatus.CREATED).build();
        }catch (ActividadNoExistente e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SolicitudFueraDePlazo | NumeroDeInvitadosIncorrecto e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }


    @PutMapping("/solicitudes")
    public ResponseEntity<DSolicitud> actualizarSolicitud(@RequestBody DSolicitud dSolicitud, Principal user) {
        try {
            var esAdmin = servicioCredenciales.loadUserByUsername(user.getName()).getAuthorities().stream().anyMatch(a->a.getAuthority().equals("ROLE_DIRECCION"));
            System.out.println("Admin : " + esAdmin);
            if(!esAdmin) {
                Solicitud solicitudActualizada = servicioSocios.modificarSolicitud(
                        serviciosAdmin.recuperarSocioPorId(dSolicitud.idSocio()),
                        dSolicitud.idActividad(),
                        dSolicitud.numAcompaniantes()
                );
                return ResponseEntity.ok(mapeador.dto(solicitudActualizada));
            }else{
                Solicitud s = serviciosAdmin.buscarSolicitud(dSolicitud.idSolicitud());

                Solicitud solicitudActualizada = servicioSocios.modificarSolicitud(
                        serviciosAdmin.recuperarSocioPorId(dSolicitud.idSocio()),
                        dSolicitud.idActividad(),
                        dSolicitud.numAcompaniantes()
                );
                int plazas  = dSolicitud.acompaniantesAceptados() - s.getAcompaniantesAceptados();
                System.out.println(plazas);
                serviciosAdmin.procesarSolicitudManualmente(solicitudActualizada,plazas);

                return ResponseEntity.ok(mapeador.dto(solicitudActualizada));
            }
        } catch (NumeroDeInvitadosIncorrecto e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (SolicitudIncorrecta e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @DeleteMapping("/solicitudes")
    public ResponseEntity<DSolicitud> cancelarSolicitud(@RequestBody DSolicitud dsolicitud) {
        try {
            Solicitud solicitudCancelada = servicioSocios.cancelarSolicitud(
                    serviciosAdmin.recuperarSocioPorId(dsolicitud.idSocio()), dsolicitud.idActividad());

            return ResponseEntity.ok(mapeador.dto(solicitudCancelada));

        } catch (SolicitudIncorrecta e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (SolicitudFueraDePlazo e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PutMapping("/clubdesocios/socios/{email}")
    public ResponseEntity<DSocio> pagarCuota(@PathVariable String email) {
        try {
            Socio socio = serviciosAdmin.recuperarSocioPorEmail(email)
                    .orElseThrow(UsuarioNoRegistrado::new);

            serviciosAdmin.pagar(socio);

            return ResponseEntity.ok(mapeador.dto(socio));

        } catch (UsuarioNoRegistrado e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}