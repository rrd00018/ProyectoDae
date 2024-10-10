package es.ujaen.dae.entidades;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Email;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.Pattern;
import java.util.ArrayList;
import java.util.HashMap;

@Validated
public class Socio {
    @Getter @Setter @Email
    private String email;
    @Getter @Setter
    private String nombre;
    @Getter @Setter
    private String apellidos;
    @Getter @Setter @Pattern(regexp="^(\\+34|0034|34)?[6789]\\d{8}$")
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
        solicitudes = new HashMap<Integer,Solicitud>();
        this.haPagado = false;
    }

    /**
     * @brief CREA UNA SOLICITUD DADA LA ACTIVIDAD Y EL NUMERO DE ACOMPAÑANTES
     * @param soli
     * @param actividad
     * @throws Exception
     */
    public void crearSolicitud(Solicitud soli, Actividad actividad) {
        solicitudes.put(soli.getIdSolicitud()/100,soli);
        actividad.addSolicitud(soli);
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
