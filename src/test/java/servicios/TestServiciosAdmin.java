package servicios;

import es.ujaen.dae.entidades.Temporada;
import es.ujaen.dae.excepciones.ClienteRegistrado;
import es.ujaen.dae.excepciones.TemporadaYaCreada;
import es.ujaen.dae.servicios.ServicioSocios;
import es.ujaen.dae.servicios.ServiciosAdmin;
import es.ujaen.dae.excepciones.FechaNoAlcanzada;
import es.ujaen.dae.excepciones.TemporadaNoExiste;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(classes = ServiciosAdmin.class)
@Validated
public class TestServiciosAdmin {
    @Autowired
    ServiciosAdmin serviciosAdmin;



    @Test
    @DirtiesContext
    public void testNuevoSocio(){
        var socio = serviciosAdmin.crearSocio("juan@gmail.com", "Juan","Torres",684190546,"1234");
        assertThat(socio).isNotNull();
    }

    @Test
    @DirtiesContext
    public void testNuevoSocioDuplicado(){
        var socio = serviciosAdmin.crearSocio("juan@gmail.com","Juan","Torres",684190546,"1234");
        assertThatThrownBy(() -> serviciosAdmin.crearSocio("juan@gmail.com","Juan","Torres",684190546,"1234")).isInstanceOf(ClienteRegistrado.class);
    }

    @Test
    @DirtiesContext
    public void testNuevoSocioSinEmail(){
        assertThatThrownBy(() -> serviciosAdmin.crearSocio("","","",1,"")).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DirtiesContext
    public void testCrearActividad() {
        int anioTemporada = LocalDate.now().getYear() + 1;
        Temporada temporada = serviciosAdmin.crearTemporada();


        String titulo = "Yoga";
        String descripcion = "Clase";
        float precio = 30;
        int plazas = 20;
        LocalDate fechaCelebracion = LocalDate.of(anioTemporada, 10, 15);
        LocalDate fechaInicioInscripcion = LocalDate.of(anioTemporada, 7, 1);
        LocalDate fechaFinInscripcion = LocalDate.of(anioTemporada, 9, 30);

        // Test: Crear la actividad y verificar que se haya registrado sin errores
        serviciosAdmin.crearActividad(titulo, descripcion, precio, plazas, fechaCelebracion, fechaInicioInscripcion, fechaFinInscripcion);

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
        int anioTemporada = LocalDate.now().getYear();
        Temporada temporada = serviciosAdmin.crearTemporada();


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
                serviciosAdmin.crearActividad(titulo, descripcion, precio, plazas, fechaCelebracion, fechaInicioInscripcion, fechaFinInscripcion)
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
                serviciosAdmin.crearActividad(titulo, descripcion, precio, plazas, fechaCelebracion, fechaInicioInscripcion, fechaFinInscripcion)
        ).isInstanceOf(TemporadaNoExiste.class);
    }

    @Test
    @DirtiesContext
    public void testCrearTemporadaExistente(){
        var temporada = serviciosAdmin.crearTemporada();
        assertThatThrownBy(() -> serviciosAdmin.crearTemporada()).isInstanceOf(TemporadaYaCreada.class);
    }


    @Test
    @DirtiesContext
    public void testCrearTemporada () {
        var temporada = serviciosAdmin.crearTemporada();
        assertThat(temporada).isNotNull();
    }



}
