package servicios;

import es.ujaen.dae.entidades.Actividad;
import es.ujaen.dae.entidades.Solicitud;
import es.ujaen.dae.entidades.Socio;
import es.ujaen.dae.entidades.Temporada;
import es.ujaen.dae.servicios.ServicioSocios;
import es.ujaen.dae.servicios.ServiciosAdmin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = es.ujaen.dae.app.ClubDeSocios.class)
public class TestServicioSocios {

    @Autowired
    private ServiciosAdmin servicioAdmin;

    @Autowired
    private ServicioSocios servicioSocios;

    @Test
    public void testEcharSolicitud_ActividadExistente() {
        Socio socio = new Socio("maria@example.com", "Maria", "Garcia", 123456789, "claveMaria123");
        LocalDate fechaCelebracion = LocalDate.of(2024, 5, 20);
        LocalDate fechaInicioInscripcion = LocalDate.of(2024, 3, 1);
        LocalDate fechaFinInscripcion = LocalDate.of(2024, 5, 10);

        Temporada temporada = servicioAdmin.crearTemporada(2024);
        servicioAdmin.crearActividad(temporada, "Yoga en el Parque", "Clases de yoga al aire libre", 30.0f, 15,
                fechaCelebracion, fechaInicioInscripcion, fechaFinInscripcion);

        Solicitud solicitud = servicioSocios.echarSolicitud(socio, 1, 3);

        assertNotNull(solicitud);
        assertEquals(3, solicitud.getNumAcompaniantes());
        assertEquals("Yoga en el Parque", solicitud.getActividad().getTitulo());
    }

    @Test
    public void testEcharSolicitud_ActividadNoExistente() {
        Socio socio = new Socio("pedro@example.com", "Pedro", "Lopez", 987654321, "clavePedro456");

        Solicitud solicitud = servicioSocios.echarSolicitud(socio, 999, 2);

        assertNull(solicitud);
    }

    @Test
    public void testModificarSolicitud() {
        Socio socio = new Socio("ana@example.com", "Ana", "Martinez", 555555555, "claveAna789");
        LocalDate fechaCelebracion = LocalDate.of(2024, 6, 10);
        LocalDate fechaInicioInscripcion = LocalDate.of(2024, 4, 5);
        LocalDate fechaFinInscripcion = LocalDate.of(2024, 6, 1);

        Temporada temporada = servicioAdmin.crearTemporada(2024);
        Actividad actividad = servicioAdmin.crearActividad(temporada, "Curso de Fotografía", "Aprende a manejar tu cámara", 100.0f, 10,
                fechaCelebracion, fechaInicioInscripcion, fechaFinInscripcion);

        servicioSocios.echarSolicitud(socio, actividad.getId(), 1);
        Solicitud solicitudModificada = servicioSocios.modificarSolicitud(socio, actividad.getId(), 2);

        assertNotNull(solicitudModificada);
        assertEquals(2, solicitudModificada.getNumAcompaniantes());
    }

    @Test
    public void testCancelarSolicitud() {
        Socio socio = new Socio("luis@example.com", "Luis", "Ramirez", 111222333, "claveLuis321");
        LocalDate fechaCelebracion = LocalDate.of(2024, 7, 25);
        LocalDate fechaInicioInscripcion = LocalDate.of(2024, 5, 15);
        LocalDate fechaFinInscripcion = LocalDate.of(2024, 7, 20);

        Temporada temporada = servicioAdmin.crearTemporada(2024);
        servicioAdmin.crearActividad(temporada, "Taller de Cocina", "Aprende recetas tradicionales", 75.0f, 12,
                fechaCelebracion, fechaInicioInscripcion, fechaFinInscripcion);

        servicioSocios.echarSolicitud(socio, 3, 1);
        Solicitud solicitudCancelada = servicioSocios.cancelarSolicitud(socio, 3);

        assertNotNull(solicitudCancelada);
    }
}
