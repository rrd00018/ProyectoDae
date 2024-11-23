package es.ujaen.dae.rest.dto;

public record DSolicitud(
        int idSolicitud,
        int numAcompaniantes,
        boolean aceptada,
        int acompaniantesAceptados,
        int idSocio,
        int idActividad
) {
}
