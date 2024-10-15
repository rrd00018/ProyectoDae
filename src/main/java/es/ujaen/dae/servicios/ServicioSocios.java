package es.ujaen.dae.servicios;

import es.ujaen.dae.entidades.Socio;
import es.ujaen.dae.entidades.Actividad;
import es.ujaen.dae.entidades.Solicitud;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.ArrayList;

@Service
@Validated
public class ServicioSocios {
    @Autowired
    private ServiciosAdmin servicioAdmin;

    public ServicioSocios() {}

    /**@brief ECHAR SOLICITUD*/
    public Solicitud echarSolicitud(Socio socio, int idActividad, int invitados) {
        Actividad actividad = servicioAdmin.buscarActividad(idActividad);
        if(actividad != null && !socio.existeSolicitud(idActividad)){
            Solicitud soli=new Solicitud(socio,invitados,actividad);
            actividad.addSolicitud(soli);
            socio.crearSolicitud(soli,actividad);
            return soli;
        }
        return null;
    }

    /**@brief MODIFICAR SOLICITUD*/
    public Solicitud modificarSolicitud(Socio socio, int idActividad, int nuevosInvitados) {
        return  socio.modificarSolicitud(idActividad,nuevosInvitados);
    }

    /**@brief CANCELAR SOLICITUD*/
    public Solicitud cancelarSolicitud(Socio socio, int idActividad) {
        return socio.cancelarSolicitud(idActividad);
    }

    /**@brief OBTIENE EL LISTADO DE SUS SOLICITUDES*/
    public ArrayList<Solicitud> obtenerSolicitudes(Socio socio) {
        return socio.obtenerSolicitudes();
    }
}
