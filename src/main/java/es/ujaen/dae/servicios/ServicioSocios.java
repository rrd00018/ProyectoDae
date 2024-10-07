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

    public Solicitud echarSolicitud(Socio socio, Integer temporada, Integer idActividad, Integer invitados){
        Actividad actividad = servicioAdmin.buscarActividad(idActividad);
        if(actividad != null && !socio.existeSolicitud(idActividad)){
            Solicitud soli=new Solicitud(socio,invitados,actividad);
            actividad.addSolicitud(soli);
            return soli;
        }
        return null;
    }

    public Solicitud modificarSolicitud(Socio socio, Integer idActividad, Integer nuevosInvitados) {
        // Buscar la solicitud existente para la actividad
        Solicitud solicitud = socio.obtenerSolicitud(idActividad);
        modificarSolicitud(socio, idActividad, nuevosInvitados);
        return solicitud;
    }


    public Solicitud cancelarSolicitud(Socio socio, Integer idActividad) {
        Solicitud solicitud = socio.obtenerSolicitud(idActividad);
        cancelarSolicitud(socio, idActividad);
        return solicitud;
    }

}
