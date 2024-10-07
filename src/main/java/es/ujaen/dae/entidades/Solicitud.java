package es.ujaen.dae.entidades;

import lombok.Getter;
import lombok.Setter;

public class Solicitud {
    @Getter
    @Setter
    private Socio socio;
    @Getter
    @Setter
    private Integer idSolicitud;
    @Getter
    @Setter
    private Boolean aceptada = false;
    @Getter
    @Setter
    private Integer numAcompaniantes;
    @Getter
    @Setter
    private Actividad actividad;

    public Solicitud(Socio socio, Integer numAcompaniantes, Actividad actividad) {
        this.socio = socio;
        this.idSolicitud = actividad.generarIdSolicitud();
        this.aceptada = false;
        this.numAcompaniantes = numAcompaniantes;
        this.actividad = actividad;
    }
}