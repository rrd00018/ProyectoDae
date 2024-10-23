package es.ujaen.dae.entidades;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import lombok.Getter;
import lombok.Setter;

@Entity
public class Solicitud {
    @Getter @Setter @Id
    private int idSolicitud;
    @Getter @Setter @Max(5)
    private int numAcompaniantes;
    @Getter
    private boolean aceptada;
    @Getter @Setter
    private int acompaniantesAceptados;

    @ManyToOne @JoinColumn(name="idSocio")
    private Socio socio;

    @Getter @Setter
    @ManyToOne @JoinColumn(name="idActividad")
    private Actividad actividad;


    public Solicitud(Socio socio, int numAcompaniantes, Actividad actividad) {
        this.socio = socio;
        this.idSolicitud = actividad.generarIdSolicitud();
        this.numAcompaniantes = numAcompaniantes;
        this.actividad = actividad;
        aceptada = false;
        acompaniantesAceptados = 0;
    }


    public Solicitud() {}


    public void aceptarSolicitud(){aceptada = true;}


    public int getIdActividad(){return actividad.getId();}

}