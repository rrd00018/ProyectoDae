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
    private Integer telefono;
    @Getter @Setter
    private String claveAcceso;
    private HashMap<Integer,Solicitud> solicitudes; //Guarda el id de la actividad y la solicitud a la misma
    @Getter @Setter
    private Boolean haPagado;

    public Socio(String email, String nombre, String apellidos, Integer telefono, String claveAcceso) {
        this.email = email;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.claveAcceso = claveAcceso;
        this.haPagado = false;
    }

    public void crearSolicitud(Actividad actividad, Integer numAcompaniantes) throws Exception {
        Solicitud solicitud_actual = new Solicitud(this,numAcompaniantes,actividad);
        solicitudes.put(solicitud_actual.getActividad().getId(),solicitud_actual);
        actividad.nuevaSolicitud(solicitud_actual);
    }

    // Verificar si ya existe una solicitud para una actividad específica
    public Boolean existeSolicitud(Integer id_actividad) {
        return solicitudes.containsKey(id_actividad);
    }

    // Obtener una solicitud específica por el ID de la actividad
    public Solicitud obtenerSolicitud(Integer idActividad) {
        return solicitudes.get(idActividad);
    }

    // Modificar una solicitud existente
    public Solicitud modificarSolicitud(Integer idActividad, Integer nuevosInvitados) {
        // Buscar la solicitud existente para la actividad
        Solicitud solicitud = obtenerSolicitud(idActividad);

        if (solicitud != null) {
            // Actualizar los datos de la solicitud
            solicitud.setNumAcompaniantes(nuevosInvitados);
            return solicitud;  // Retornar la solicitud actualizada
        }
        return null;  // Retornar null si no existe la solicitud
    }

    // Cancelar una solicitud existente
    public Solicitud cancelarSolicitud(Integer idActividad) {
        // Buscar la solicitud existente para la actividad
        Solicitud solicitud = obtenerSolicitud(idActividad);

        if (solicitud != null) {
            // Eliminar la solicitud tanto del socio como de la actividad
            solicitudes.remove(idActividad);
            solicitud.getActividad().deleteSolicitud(solicitud);
            return solicitud;  // Retornar la solicitud cancelada
        }
        return null;  // Retornar null si no existe la solicitud
    }

}
