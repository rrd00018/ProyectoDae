package es.ujaen.dae.entidades;
import es.ujaen.dae.excepciones.SolicitudFueraDePlazo;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Entity
@Validated
public class Socio {
    @Id @Getter @Setter
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idSocio;
    @Getter @Setter @Email
    @Column(unique = true)
    private String email;
    @Getter @Setter @NotBlank
    private String nombre;
    @Getter @Setter @NotBlank
    private String apellidos;
    @Getter @Setter @Pattern(regexp="^(\\+34|0034|34)?[6789]\\d{8}$")
    private String telefono;
    @Getter @Setter @NotBlank
    private String claveAcceso;
    @Getter @Setter
    private boolean haPagado;

    @OneToMany(mappedBy = "socio", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Map<Integer, Solicitud> solicitudes; // Guarda el id de la actividad y la solicitud a la misma

    public Socio(@NotBlank @Email String email, @NotBlank String nombre, @NotBlank String apellidos,
                 @Pattern(regexp="^(\\+34|0034|34)?[6789]\\d{8}$") String telefono, @NotBlank String claveAcceso) {
        this.email = email;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.claveAcceso = claveAcceso;
        this.haPagado = false;
        this.solicitudes = new HashMap<>();
    }

    public Socio(int idSocio, String email, String nombre, String apellidos, String telefono, String claveAcceso, boolean haPagado) {
        this.idSocio = idSocio;
        this.email = email;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.claveAcceso = claveAcceso;
        this.haPagado = haPagado;
        this.solicitudes = new HashMap<>();
    }

    public Socio() {
        this.solicitudes = new HashMap<>();
    }


    /**
     *  CREA UNA SOLICITUD DADA LA ACTIVIDAD Y EL NUMERO DE ACOMPAÑANTES
     */
    public void crearSolicitud(Solicitud soli, Actividad actividad) {
        solicitudes.put(actividad.getId(),soli);
    }


    /**
     *   MODIFICAR EL NUMERO DE ACOMPAÑANTES DE UNA SOLICITUD DADA SU ID
     */
    public Solicitud modificarSolicitud(int idActividad, int nuevosInvitados) {
        Solicitud solicitud = obtenerSolicitud(idActividad);

        if (solicitud != null) {
            if(solicitud.getActividad().getFechaFinInscripcion().isBefore(LocalDate.now()))
                throw new SolicitudFueraDePlazo();
            else {
                // Actualizar los datos de la solicitud
                solicitud.setNumAcompaniantes(nuevosInvitados);
                return solicitud;  // Retornar la solicitud actualizada
            }
        }
        return null;  // Retornar null si no existe la solicitud
    }


    /**
     *   CANCELAR UNA SOLICITUD DADA SU ID
     */
    public Solicitud cancelarSolicitud(int idActividad) {
        Solicitud solicitud = obtenerSolicitud(idActividad);

        if (solicitud != null) {
            if(solicitud.getActividad().getFechaFinInscripcion().isBefore(LocalDate.now()))
                throw new SolicitudFueraDePlazo();
            else {
                solicitudes.remove(idActividad);
                solicitud.getActividad().deleteSolicitud(solicitud);
                return solicitud;
            }
        }
        return null;
    }


    /**
     *   DEVUELVE SI EXISTE UNA SOLICITUD PARA UNA ACTIVIDAD DADA
     */
    public Boolean existeSolicitud(int idActividad) {
        return solicitudes.containsKey(idActividad);
    }


    /**
     *   DEVUELVE LA SOLICITUD DADA EL ID DE ACTIVIDAD
     */
    public Solicitud obtenerSolicitud(int idActividad) {
        return solicitudes.get(idActividad);
    }


    /**
     *   DEVUELVE LAS SOLICITUDES DE ESTE SOCIO EN ARRAYLIST
     */
    public ArrayList<Solicitud> obtenerSolicitudes() {
        return new ArrayList<>(solicitudes.values());
    }

    /**
     * Operación usada para cargar las solicitudes
     */
    public int numeroSolicitudes() {
        return solicitudes.size();
    }

}
