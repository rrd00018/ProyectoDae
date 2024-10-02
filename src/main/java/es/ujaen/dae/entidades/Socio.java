package es.ujaen.dae.entidades;
import java.util.ArrayList;
import java.util.HashMap;

public class Socio {
    private String email;
    private String nombre;
    private String apellidos;
    private Integer telefono;
    private String claveAcceso;
    private HashMap<Integer,Solicitud> solicitudes;

    public Socio(String email, String nombre, String apellidos, Integer telefono, String claveAcceso) {
        this.email = email;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.claveAcceso = claveAcceso;
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

    public void crearSolicitud(Actividad actividad, Integer numAcompañantes){
        Solicitud solicitud_actual = new Solicitud(false,numAcompañantes,actividad);
        solicitudes.put(solicitud_actual.getIdSolicitud(),solicitud_actual);
        actividad.nuevaSolicitud(solicitud_actual);
    }

}
