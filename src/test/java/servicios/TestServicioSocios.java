package servicios;

import es.ujaen.dae.entidades.Actividad;
import es.ujaen.dae.entidades.Solicitud;
import es.ujaen.dae.excepciones.ActividadNoExistente;
import es.ujaen.dae.repositorios.RepositorioActividad;
import es.ujaen.dae.servicios.ServicioSocios;
import es.ujaen.dae.servicios.ServiciosAdmin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest(classes = es.ujaen.dae.app.ClubDeSocios.class)
@ActiveProfiles("test")
public class TestServicioSocios {

    @Autowired
    private ServiciosAdmin serviciosAdmin;

    @Autowired
    private ServicioSocios servicioSocios;
    @Autowired
    private RepositorioActividad repositorioActividad;

    @BeforeEach
    public void setUp() {
        // Registra los socios en el repositorio (simula un registro)
        serviciosAdmin.crearSocio("carlos@example.com", "Carlos", "Perez", "600000001", "claveCarlos123");
        serviciosAdmin.crearSocio("elena@example.com", "Elena", "Gomez", "600000002", "claveElena456");
        serviciosAdmin.crearSocio("raul@example.com", "Raul", "Martinez", "600000003", "claveRaul789");
        serviciosAdmin.crearSocio("laura@example.com", "Laura", "Sanchez", "600000004", "claveLaura321");
    }

    @Test
    @DirtiesContext
    public void testEcharSolicitud_ActividadExistente() {
        // Simulación de login para Carlos
        var socioCarlos = serviciosAdmin.login("carlos@example.com", "claveCarlos123").get();

        LocalDate fechaCelebracion = LocalDate.now().plusDays(10);
        LocalDate fechaInicioInscripcion = LocalDate.now().minusDays(10);
        LocalDate fechaFinInscripcion = LocalDate.now().plusDays(1);
        serviciosAdmin.crearTemporada();
        var a  = serviciosAdmin.crearActividad("Pilates en la Playa", "Clases de pilates al aire libre", 40.0f, 20, fechaCelebracion, fechaInicioInscripcion, fechaFinInscripcion);

        List<Actividad> actividadesAbiertas = serviciosAdmin.listarActividadesDisponibles();
        // REFRESCAR socioCarlos HACIENDO UN LOGIN
        socioCarlos = serviciosAdmin.login("carlos@example.com", "claveCarlos123").get();
        Solicitud solicitud = servicioSocios.echarSolicitud(socioCarlos, actividadesAbiertas.get(0).getId(), 2);

        assertNotNull(solicitud);
        assertEquals(2, solicitud.getNumAcompaniantes());
        assertEquals("Pilates en la Playa", solicitud.getActividad().getTitulo());
    }

    @Test
    @DirtiesContext
    public void testEcharSolicitud_ActividadNoExistente() {
        // Simulación de login para Elena
        var socioElena = serviciosAdmin.login("elena@example.com", "claveElena456").get();

        serviciosAdmin.crearTemporada();
        assertThatThrownBy(() -> servicioSocios.echarSolicitud(socioElena, 999, 1)).isInstanceOf(ActividadNoExistente.class);
    }

    @Test
    @DirtiesContext
    public void testModificarSolicitud() {
        // Simulación de login para Raul
        var socioRaul = serviciosAdmin.login("raul@example.com", "claveRaul789").get();

        LocalDate fechaCelebracion = LocalDate.now().plusDays(10);
        LocalDate fechaInicioInscripcion = LocalDate.now().minusDays(10);
        LocalDate fechaFinInscripcion = LocalDate.now().plusDays(1);
        serviciosAdmin.crearTemporada();
        serviciosAdmin.crearActividad("Taller de Pintura", "Aprende técnicas de pintura al óleo", 120.0f, 15,
                fechaCelebracion, fechaInicioInscripcion, fechaFinInscripcion);

        List<Actividad> actividadesAbiertas = serviciosAdmin.listarActividadesDisponibles();

        servicioSocios.echarSolicitud(socioRaul, actividadesAbiertas.get(0).getId(), 1);

        ArrayList<Solicitud> solicitudesSocio = servicioSocios.obtenerSolicitudes(socioRaul);

        Solicitud solicitudModificada = servicioSocios.modificarSolicitud(socioRaul, solicitudesSocio.get(0).getActividad().getId(), 3);

        assertNotNull(solicitudModificada);
        assertEquals(3, solicitudModificada.getNumAcompaniantes());
    }

    @Test
    @DirtiesContext
    public void testCancelarSolicitud() {
        // Simulación de login para Laura
        var socioLaura = serviciosAdmin.login("laura@example.com", "claveLaura321").get();

        LocalDate fechaCelebracion = LocalDate.now().plusDays(10);
        LocalDate fechaInicioInscripcion = LocalDate.now().minusDays(10);
        LocalDate fechaFinInscripcion = LocalDate.now().plusDays(1);
        serviciosAdmin.crearTemporada();
        serviciosAdmin.crearActividad("Clases de Cocina Vegetariana", "Explora la cocina vegetariana", 90.0f, 18,
                fechaCelebracion, fechaInicioInscripcion, fechaFinInscripcion);

        List<Actividad> actividadesAbiertas = serviciosAdmin.listarActividadesDisponibles();
        servicioSocios.echarSolicitud(socioLaura, actividadesAbiertas.get(0).getId(), 1);
        ArrayList<Solicitud> solicitudesSocio = servicioSocios.obtenerSolicitudes(socioLaura);
        Solicitud solicitudCancelada = servicioSocios.cancelarSolicitud(socioLaura, solicitudesSocio.get(0).getActividad().getId());

        var a = serviciosAdmin.buscarActividad(solicitudCancelada.getActividad().getId()).getSolicitudes();
        assertNotNull(solicitudCancelada);
        assertThat(a.size()).isEqualTo(0);
    }
}
