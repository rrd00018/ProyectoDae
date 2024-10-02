package es.ujaen.dae.entidades;

public class Solicitud {
    private Integer idSolicitud;
    private boolean aceptada = false;
    private Integer NumAcompaniantes;
    private Actividad actividad;

    public Solicitud(boolean estado_aceptada, Integer num_acompaniantes, Actividad _actividad) {
        this.idSolicitud = _actividad.generarIdSolicitud();
        this.aceptada = estado_aceptada;
        this.NumAcompaniantes = num_acompaniantes;
        this.actividad = _actividad;
    }
    //getters y setters
    public Integer getIdSolicitud() {return idSolicitud;}
    public Integer getNumAcompañantes() {return NumAcompaniantes;}
    public void modificar_num_acompañantes(Integer nuevo_numero){NumAcompaniantes = nuevo_numero;}
    public void setNuevo_estado(boolean nuevo_estado){aceptada = nuevo_estado;}
    public boolean getNuevo_estado(){return aceptada;}
    public Actividad getActividad() {return actividad;}


}
