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
     * @brief ECHAR SOLICITUD
     */
    @Transactional
    public Solicitud echarSolicitud(Optional<Socio> socio, int idActividad, int invitados) {
        Actividad actividad = repositorioActividad.buscar(idActividad)
                .orElseThrow(ActividadNoExistente::new);

        if (actividad.getFechaFinInscripcion().isBefore(LocalDate.now())) {
            throw new SolicitudFueraDePlazo();
        }

        if (invitados < 0 || invitados > 5) {
            throw new NumeroDeInvitadosIncorrecto();
        }

        if (!socio.isPresent()) {
            throw new IllegalArgumentException("El socio no puede estar vacío");
        }

        if (!socio.get().existeSolicitud(idActividad)) {
            Solicitud soli = new Solicitud(socio.get(), invitados, actividad);
            actividad.addSolicitud(soli);
            socio.get().crearSolicitud(soli, actividad);

            return soli;
        }
        return null;
    }



    /**
     * @brief MODIFICAR SOLICITUD
     */
    @Transactional
    public Solicitud modificarSolicitud(@Valid java.util.Optional<Socio> socio, int idActividad, int nuevosInvitados) {
        if(nuevosInvitados < 0 || nuevosInvitados > 5){
            throw new NumeroDeInvitadosIncorrecto();
        }
        Solicitud s = socio.get().modificarSolicitud(idActividad,nuevosInvitados);
        return repositorioSolicitud.actualizar(s);

    }


    /**
     * @brief CANCESAR SOLICITUD
     */
    @Transactional
    public Solicitud cancelarSolicitud(@Valid java.util.Optional<Socio> socio, int idActividad) {
        return repositorioSolicitud.borrarSolicitud(socio.get(),idActividad);
    }


    /**
     * @brief OBTIENE LAS SOLICITUDES
     */
    public ArrayList<Solicitud> obtenerSolicitudes(@Valid java.util.Optional<Socio> socio) {
        return socio.get().obtenerSolicitudes();
    }
}
