package es.ujaen.dae.rest.dto;

import org.springframework.cglib.core.Local;

import java.time.LocalDate;

public record DActividad(
        int id,
        String titulo,
        String descripcion,
        float precio,
        int plazas,
        LocalDate fechaCelebracion,
        LocalDate fechaInicioInscripcion,
        LocalDate fechaFinInscripcion,
        int plazasAsignadas
) {
}
