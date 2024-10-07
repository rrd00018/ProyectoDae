package es.ujaen.dae.servicios;

import es.ujaen.dae.entidades.Socio;
import es.ujaen.dae.entidades.Actividad;
import es.ujaen.dae.entidades.Solicitud;
import es.ujaen.dae.entidades.Actividad;
import es.ujaen.dae.entidades.Temporada;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;

@Service
public class ServicioSocios {
    @Autowired
    private ServiciosAdmin servicioAdmin;

    public ServicioSocios() {}

    /** @brief ECHAR SOLICITUD */
    public void echarSolicitud(String mailSocio, Integer idActividad, Integer invitados) throws Exception {
        servicioAdmin.crearSolicitud(mailSocio, idActividad, invitados);
    }

    /** @brief MODIFICAR SOLICITUD */
    public void modificarSolicitud(String mailSocio, Integer idActividad, Integer nuevosInvitados) throws Exception {
        servicioAdmin.modificarSolicitud(mailSocio, idActividad, nuevosInvitados);
    }

    /** @brief CANCELAR SOLICITUD */
    public void cancelarSolicitud(String mailSocio, Integer idActividad) throws Exception {
        servicioAdmin.cancelarSolicitud(mailSocio, idActividad);
    }
}

