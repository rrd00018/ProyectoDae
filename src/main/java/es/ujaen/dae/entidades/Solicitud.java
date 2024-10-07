package es.ujaen.dae.entidades;

import lombok.Getter;
import lombok.Setter;

public class Solicitud {
    @Getter @Setter
    private Socio socio;
    @Getter @Setter
    private Integer idSolicitud;
    @Getter @Setter
    private Boolean aceptada = false;
    @Getter @Setter
    private Actividad actividad;
    @Getter
    private Integer numAcompaniantes;

    public Solicitud(Socio socio, Integer numAcompaniantes, Actividad actividad) {
        this.socio = socio;
        this.aceptada = false;
        this.numAcompaniantes = numAcompaniantes;
        this.actividad = actividad;
        this.idSolicitud = actividad.generarIdSolicitud();
    }

    public void setNumAcompaniantes(Integer numAcompaniantes) throws Exception {
        this.numAcompaniantes = numAcompaniantes;
        actividad.modificarSolicitud(this, numAcompaniantes);
    }
}