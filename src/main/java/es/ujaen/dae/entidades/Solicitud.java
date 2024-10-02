package es.ujaen.dae.entidades;

public class Solicitud {
    private Integer idSolicitud;
    private Boolean aceptada = false;
    private Integer numAcompaniantes;
    private Actividad actividad;
    private Socio socio;

    public Solicitud(Boolean estadoAceptada, Integer numAcompaniantes, Actividad actividad) {
        this.idSolicitud = actividad.generarIdSolicitud();
        this.aceptada = estadoAceptada;
        this.numAcompaniantes = numAcompaniantes;
        this.actividad = actividad;
    }
    //getters y setters
    public Integer getIdSolicitud() {return idSolicitud;}
    public Integer getNumAcompañantes() {return numAcompaniantes;}
    public void modificarNumAcompañantes(Integer nuevoNumero){
        numAcompaniantes = nuevoNumero;}
    public void setNuevoEstado(boolean nuevoEstado){aceptada = nuevoEstado;}
    public Boolean getNuevoEstado(){return aceptada;}
    public Actividad getActividad() {return actividad;}
    public Socio getSocio() {return socio;}
}
