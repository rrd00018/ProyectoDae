package es.ujaen.dae.servicios;

import es.ujaen.dae.entidades.Actividad;
import es.ujaen.dae.entidades.Socio;
import es.ujaen.dae.entidades.Solicitud;
import es.ujaen.dae.excepciones.ActividadNoExistente;
import es.ujaen.dae.excepciones.NumeroDeInvitadosIncorrecto;
import es.ujaen.dae.excepciones.SolicitudFueraDePlazo;
import es.ujaen.dae.repositorios.RepositorioActividad;
import es.ujaen.dae.repositorios.RepositorioSocio;
import es.ujaen.dae.repositorios.RepositorioSolicitud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

@Service
@Validated
public class ServicioSocios {
    @Autowired
    private RepositorioSolicitud repositorioSolicitud;
    @Autowired
    private RepositorioActividad repositorioActividad;
    @Autowired
    private RepositorioSocio repositorioSocio;
    @Autowired
    private ServiciosAdmin serviciosAdmin;

    public ServicioSocios() {}

    /**
     *  ECHAR SOLICITUD
     */
    @Transactional
    public Solicitud echarSolicitud(Socio socio, int idActividad, int invitados) {
        Actividad actividad = repositorioActividad.buscar(idActividad)
                .orElseThrow(ActividadNoExistente::new);

        if (actividad.getFechaFinInscripcion().isBefore(LocalDate.now())) {
            throw new SolicitudFueraDePlazo();
        }

        if (invitados < 0 || invitados > 5) {
            throw new NumeroDeInvitadosIncorrecto();
        }

        if ( actividad.getPlazasAsignadas() == actividad.getPlazas()){
            throw new NumeroDeInvitadosIncorrecto();   // si ya se han asignado el total de plazas no se pueden echar mas solicitudes
        }

        socio = repositorioSocio.actualizar(socio);
        Solicitud soli = new Solicitud(socio, invitados, actividad);
        boolean reservado = false;
        while (!reservado) {
            try {
                socio.crearSolicitud(soli, actividad);
                repositorioSolicitud.guardar(soli);
                if(socio.isHaPagado()){
                    actividad.addSolicitud(soli);
                }
                repositorioSolicitud.comprobarErrores();
                reservado = true;
            }
            catch(OptimisticLockingFailureException e) {
            }
        }

        if (!socio.existeSolicitud(idActividad)) {
            repositorioSocio.actualizar(socio);
            repositorioActividad.actualizar(actividad);
            return soli;
        }

        return null;
    }



    /**
     *  MODIFICAR SOLICITUD
     */
    @Transactional
    public Solicitud modificarSolicitud(@Valid Socio socio, int idActividad, int nuevosInvitados) {
        if(nuevosInvitados < 0 || nuevosInvitados > 5){
            throw new NumeroDeInvitadosIncorrecto();
        }
        socio = repositorioSocio.actualizar(socio);
        socio.numeroSolicitudes();
        Solicitud s = socio.modificarSolicitud(idActividad,nuevosInvitados);
        return repositorioSolicitud.actualizar(s);

    }


    /**
     *  CANCESAR SOLICITUD
     */
    @Transactional
    public Solicitud cancelarSolicitud(@Valid Socio socio, int idActividad) {
        socio = repositorioSocio.actualizar(socio);
        Solicitud solicitud = socio.cancelarSolicitud(idActividad);
        if(solicitud==null){
            throw new SolicitudFueraDePlazo();
        }
        Actividad actividad = solicitud.getActividad();
        repositorioSolicitud.borrarSolicitud(solicitud);
        repositorioActividad.actualizar(actividad);
        return solicitud;
    }


    /**
     *  Devuelve una lista con las solicitudes de un socio
     */
    @Transactional
    public ArrayList<Solicitud> obtenerSolicitudes(@Valid Socio socio) {
        socio = repositorioSocio.actualizar(socio);
        return socio.obtenerSolicitudes();
    }

}
