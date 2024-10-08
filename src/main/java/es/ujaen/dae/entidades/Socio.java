package es.ujaen.dae.entidades;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;

public class Socio {
    @Getter @Setter
    private String email;
    @Getter @Setter
    private String nombre;
    @Getter @Setter
    private String apellidos;
    @Getter @Setter
    private int telefono;
    @Getter @Setter
    private String claveAcceso;
    private HashMap<Integer,Solicitud> solicitudes; //Guarda el id de la actividad y la solicitud a la misma
    @Getter @Setter
    private boolean haPagado;


    public Socio(String email, String nombre, String apellidos, int telefono, String claveAcceso) {
        this.email = email;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.claveAcceso = claveAcceso;
        this.haPagado = false;
    }

    /**
     * @brief CREA UNA SOLICITUD DADA LA ACTIVIDAD Y EL NUMERO DE ACOMPAÑANTES
     * @param actividad
     * @param numAcompaniantes
     * @throws Exception
     */
    public void crearSolicitud(Actividad actividad, int numAcompaniantes) throws Exception {
        Solicitud solicitud_actual = new Solicitud(this,numAcompaniantes,actividad);
        solicitudes.put(solicitud_actual.getActividad().getId(),solicitud_actual);
        actividad.addSolicitud(solicitud_actual);
    }


    /**
     * @brief MODIFICAR EL NUMERO DE ACOMPAÑANTES DE UNA SOLICITUD DADA SU ID
     * @param idActividad
     * @param nuevosInvitados
     * @return
     */
    public Solicitud modificarSolicitud(int idActividad, int nuevosInvitados) {
        Solicitud solicitud = obtenerSolicitud(idActividad);

        if (solicitud != null) {
            // Actualizar los datos de la solicitud
            solicitud.setNumAcompaniantes(nuevosInvitados);
            return solicitud;  // Retornar la solicitud actualizada
        }
        return null;  // Retornar null si no existe la solicitud
    }


    /**
     * @brief CANCELAR UNA SOLICITUD DADA SU ID
     * @param idActividad
     * @return
     */
    public Solicitud cancelarSolicitud(int idActividad) {
        Solicitud solicitud = obtenerSolicitud(idActividad);

        if (solicitud != null) {
            solicitudes.remove(idActividad);
            solicitud.getActividad().deleteSolicitud(solicitud);
            return solicitud;
        }
        return null;
    }


    /**
     * @brief DEVUELVE SI EXISTE UNA SOLICITUD PARA UNA ACTIVIDAD DADA
     * @param id_actividad
     * @return
     */
    public Boolean existeSolicitud(int id_actividad) {
        return solicitudes.containsKey(id_actividad);
    }


    /**
     * @brief DEVUELVE LA SOLICITUD DADA EL ID DE ACTIVIDAD
     * @param idActividad
     * @return
     */
    public Solicitud obtenerSolicitud(int idActividad) {
        return solicitudes.get(idActividad);
    }

}
