package es.ujaen.dae.entidades;

public class Solicitud {
    private Integer idSolicitud;
    private Boolean aceptada = false;
    private Integer NumAcompaniantes;
    private Actividad actividad;

    public Solicitud(boolean estadoAceptada, Integer numAcompaniantes, Actividad actividad) {
        this.idSolicitud = actividad.generarIdSolicitud();
        this.aceptada = estadoAceptada;
        this.NumAcompaniantes = numAcompaniantes;
        this.actividad = actividad;
    }
    //getters y setters
    public Integer getIdSolicitud() {return idSolicitud;}
    public Integer getNumAcompañantes() {return NumAcompaniantes;}
    public void modificarNumAcompañantes(Integer nuevoNumero){NumAcompaniantes = nuevoNumero;}
    public void setNuevoEstado(boolean nuevoEstado){aceptada = nuevoEstado;}
    public Boolean getNuevoEstado(){return aceptada;}
    public Actividad getActividad() {return actividad;}


}
