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

        socio = repositorioSocio.actualizar(socio);
        if (!socio.existeSolicitud(idActividad)) {

            Solicitud soli = new Solicitud(socio, invitados, actividad);
            soli.getSocio().crearSolicitud(soli, actividad);

            repositorioSolicitud.guardar(soli);
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
        Actividad actividad = solicitud.getActividad();
        repositorioSolicitud.actualizar(solicitud);
        repositorioActividad.actualizar(actividad);
        return solicitud;
    }


    /**
     *  OBTIENE LAS SOLICITUDES
     */
    public ArrayList<Solicitud> obtenerSolicitudes(@Valid Socio socio) {
        socio = repositorioSocio.actualizar(socio);
        socio.numeroSolicitudes();
        return socio.obtenerSolicitudes();
    }
}
