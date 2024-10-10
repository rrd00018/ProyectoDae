package servicios;

import es.ujaen.dae.entidades.Temporada;
import es.ujaen.dae.servicios.ServiciosAdmin;
import es.ujaen.dae.excepciones.FechaNoAlcanzada;
import es.ujaen.dae.excepciones.TemporadaNoExiste;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = ServiciosAdmin.class)
public class TestServiciosAdmin {
    @Autowired
    ServiciosAdmin serviciosAdmin;

    @Test
    @DirtiesContext
    public void testCrearActividad() {
        int anioTemporada = LocalDate.now().getYear() + 1;
        Temporada temporada = serviciosAdmin.crearTemporada(anioTemporada);


        String titulo = "Yoga";
        String descripcion = "Clase";
        float precio = 30;
        int plazas = 20;
        LocalDate fechaCelebracion = LocalDate.of(anioTemporada, 10, 15);
        LocalDate fechaInicioInscripcion = LocalDate.of(anioTemporada, 7, 1);
        LocalDate fechaFinInscripcion = LocalDate.of(anioTemporada, 9, 30);

        // Test: Crear la actividad y verificar que se haya registrado sin errores
        serviciosAdmin.crearActividad(temporada, titulo, descripcion, precio, plazas, fechaCelebracion, fechaInicioInscripcion, fechaFinInscripcion);

        // Verificación: Buscar la actividad en la temporada
        var actividad = serviciosAdmin.buscarActividad(fechaCelebracion.getYear() * 1000 );

        assertThat(actividad).isNotNull();
        assertThat(actividad.getTitulo()).isEqualTo(titulo);
        assertThat(actividad.getDescripcion()).isEqualTo(descripcion);
        assertThat(actividad.getPrecio()).isEqualTo(precio);
    }

    @Test
    public void testCrearActividadConFechasIncorrectas() {
        // Crear una temporada primero para asociar la actividad
        int anioTemporada = LocalDate.now().getYear() + 1;
        Temporada temporada = serviciosAdmin.crearTemporada(anioTemporada);

        // Datos de la actividad con fechas incorrectas
        String titulo = "Clase";
        String descripcion = "Clase ";
        float precio = 50.0f;
        int plazas = 15;
        LocalDate fechaCelebracion = LocalDate.of(anioTemporada, 5, 15);
        LocalDate fechaInicioInscripcion = LocalDate.of(anioTemporada, 6, 1);
        LocalDate fechaFinInscripcion = LocalDate.of(anioTemporada, 7, 1);

        // Verificación: Debe lanzar una excepción de FechaNoAlcanzada
        assertThatThrownBy(() ->
                serviciosAdmin.crearActividad(temporada, titulo, descripcion, precio, plazas, fechaCelebracion, fechaInicioInscripcion, fechaFinInscripcion)
        ).isInstanceOf(FechaNoAlcanzada.class);
    }

    @Test
    public void testCrearActividadConTemporadaInexistente() {
        // Datos de una temporada no registrada
        Temporada temporadaInexistente = new Temporada(LocalDate.now().getYear() + 2);

        // Datos de la actividad
        String titulo = "Entrenamiento";
        String descripcion = "Clase";
        float precio = 20.0f;
        int plazas = 10;
        LocalDate fechaCelebracion = LocalDate.of(2025, 6, 15);
        LocalDate fechaInicioInscripcion = LocalDate.of(2025, 3, 1);
        LocalDate fechaFinInscripcion = LocalDate.of(2025, 4, 30);

        // Verificación: Debe lanzar una excepción de TemporadaNoExiste
        assertThatThrownBy(() ->
                serviciosAdmin.crearActividad(temporadaInexistente, titulo, descripcion, precio, plazas, fechaCelebracion, fechaInicioInscripcion, fechaFinInscripcion)
        ).isInstanceOf(TemporadaNoExiste.class);
    }
}
