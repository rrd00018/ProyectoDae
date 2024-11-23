package es.ujaen.dae.rest.dto;

public record DSocio(
        int idSocio,
        String email,
        String nombre,
        String apellidos,
        String telefono,
        String claveAcceso,
        boolean haPagado
) {
}
