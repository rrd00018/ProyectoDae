package es.ujaen.dae.servicios;

import es.ujaen.dae.entidades.Socio;
import es.ujaen.dae.entidades.Actividad;
import es.ujaen.dae.entidades.Solicitud;
import es.ujaen.dae.excepciones.ActividadNoExistente;
import es.ujaen.dae.excepciones.NumeroDeInvitadosIncorrecto;
import es.ujaen.dae.excepciones.SolicitudFueraDePlazo;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.ArrayList;

@Service
@Validated
public class ServicioSocios {
    @Autowired
    private ServiciosAdmin servicioAdmin;

    public ServicioSocios() {}

    /** ECHAR SOLICITUD*/
    public Solicitud echarSolicitud(@Valid Socio socio, int idActividad, int invitados) {
        Actividad actividad = servicioAdmin.buscarActividad(idActividad);
        if(actividad == null){
            throw new ActividadNoExistente();
        }
        if(actividad.getFechaFinInscripcion().isBefore(LocalDate.now())){
            throw new SolicitudFueraDePlazo();
        }
        if(invitados < 0 || invitados > 5){
            throw new NumeroDeInvitadosIncorrecto();
        }
        if(!socio.existeSolicitud(idActividad)){
            Solicitud soli=new Solicitud(socio,invitados,actividad);
            actividad.addSolicitud(soli);
            socio.crearSolicitud(soli,actividad);
            return soli;
        }
        return null;
    }

    /** MODIFICAR SOLICITUD*/
    public Solicitud modificarSolicitud(@Valid Socio socio, int idActividad, int nuevosInvitados) {
        if(nuevosInvitados < 0 || nuevosInvitados > 5){
            throw new NumeroDeInvitadosIncorrecto();
        }
        return  socio.modificarSolicitud(idActividad,nuevosInvitados);
    }

    /** CANCELAR SOLICITUD*/
    public Solicitud cancelarSolicitud(@Valid Socio socio, int idActividad) {
        return socio.cancelarSolicitud(idActividad);
    }

    /** OBTIENE EL LISTADO DE SUS SOLICITUDES*/
    public ArrayList<Solicitud> obtenerSolicitudes(@Valid Socio socio) {
        return socio.obtenerSolicitudes();
    }
}
