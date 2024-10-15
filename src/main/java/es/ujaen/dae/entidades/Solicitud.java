package es.ujaen.dae.entidades;

import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.Setter;

public class Solicitud {
    @Getter @Setter
    private Socio socio;
    @Getter @Setter
    private int idSolicitud;
    @Getter @Setter @Max(5)
    private int numAcompaniantes;
    @Getter @Setter
    private Actividad actividad;
    @Getter
    private boolean aceptada;
    @Getter @Setter
    private int acompaniantesAceptados;

    public Solicitud(Socio socio, int numAcompaniantes, Actividad actividad) {
        this.socio = socio;
        this.idSolicitud = actividad.generarIdSolicitud();
        this.numAcompaniantes = numAcompaniantes;
        this.actividad = actividad;
        aceptada = false;
        acompaniantesAceptados = 0;
    }

    public void aceptarSolicitud(){aceptada = true;}

    public int getIdActividad(){return actividad.getId();}
}