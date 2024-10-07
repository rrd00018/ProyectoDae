package es.ujaen.dae.entidades;
import java.util.ArrayList;
import java.util.HashMap;

public class Socio {
    private String email;
    private String nombre;
    private String apellidos;
    private Integer telefono;
    private String claveAcceso;
    private HashMap<Integer,Solicitud> solicitudes; //Guarda el id de la actividad y la solicitud a la misma
    private Boolean haPagado;

    public Socio(String email, String nombre, String apellidos, Integer telefono, String claveAcceso) {
        this.email = email;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.claveAcceso = claveAcceso;
        this.haPagado = false;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Integer getTelefono() {
        return telefono;
    }
    public void setTelefono(Integer telefono) {
        this.telefono = telefono;
    }

    public String getClaveAcceso() {
        return claveAcceso;
    }
    public void setClaveAcceso(String claveAcceso) {
        this.claveAcceso = claveAcceso;
    }

    public void crearSolicitud(Actividad actividad, Integer numAcompaniantes){
        Solicitud solicitud_actual = new Solicitud(this,numAcompaniantes,actividad);
        solicitudes.put(solicitud_actual.getActividad().getId(),solicitud_actual);
        actividad.addSolicitud(solicitud_actual);
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
