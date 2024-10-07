package es.ujaen.dae.entidades;

public class Solicitud {
    private Socio socio;
    private Integer idSolicitud;
    private Boolean aceptada = false;
    private Integer numAcompaniantes;
    private Actividad actividad;

    public Solicitud(Socio socio, Integer numAcompaniantes, Actividad actividad) {
        this.socio=socio;
        this.idSolicitud = actividad.generarIdSolicitud();
        this.aceptada = false;
        this.numAcompaniantes = numAcompaniantes;
        this.actividad = actividad;
    }

    public Integer getIdSolicitud() {return idSolicitud;}
    public void setIdSolicitud(Integer nuevoIdSolicitud) {idSolicitud=nuevoIdSolicitud;}

    public Integer getNumAcompaniantes() {return numAcompaniantes;}
    public void setNumAcompaniantes(Integer nuevoNumero){numAcompaniantes = nuevoNumero;}

    public Boolean getNuevoEstado(){return aceptada;}
    public void setNuevoEstado(boolean nuevoEstado){aceptada = nuevoEstado;}

    public Actividad getActividad() {return actividad;}
    public Socio getSocio() {return socio;}
}
