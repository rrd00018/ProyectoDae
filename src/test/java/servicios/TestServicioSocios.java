package servicios;

import es.ujaen.dae.entidades.Actividad;
import es.ujaen.dae.entidades.Socio;
import es.ujaen.dae.entidades.Solicitud;
import es.ujaen.dae.excepciones.ActividadNoExistente;
import es.ujaen.dae.excepciones.NumeroDePlazasIncorrecto;
import es.ujaen.dae.excepciones.UsuarioNoRegistrado;
import es.ujaen.dae.repositorios.RepositorioActividad;
import es.ujaen.dae.rest.dto.DSocio;
import es.ujaen.dae.servicios.ServicioSocios;
import es.ujaen.dae.servicios.ServiciosAdmin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import java.util.logging.Logger;

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
        Socio carlos = new Socio(0,"carlos@example.com", "Carlos", "Perez", "600000001", "claveCarlos123",false);
        Socio elena = new Socio(0,"elena@example.com", "Elena", "Gomez", "600000002", "claveElena456",false);
        Socio raul = new Socio(0,"raul@example.com", "Raul", "Martinez", "600000003", "claveRaul789",false);
        Socio laura = new Socio(0,"laura@example.com", "Laura", "Sanchez", "600000004", "claveLaura321",false);
        serviciosAdmin.crearSocio(carlos);
        serviciosAdmin.crearSocio(elena);
        serviciosAdmin.crearSocio(raul);
        serviciosAdmin.crearSocio(laura);
    }

    @Test
    @DirtiesContext
    public void testEcharSolicitud_ActividadExistente() {
        // Simulación de login para Carlos
        var socioCarlos = serviciosAdmin.recuperarSocioPorEmail("carlos@example.com").orElseThrow(UsuarioNoRegistrado::new);

        LocalDate fechaCelebracion = LocalDate.now().plusDays(10);
        LocalDate fechaInicioInscripcion = LocalDate.now().minusDays(10);
        LocalDate fechaFinInscripcion = LocalDate.now().plusDays(1);
        serviciosAdmin.crearTemporada();
        var a  = serviciosAdmin.crearActividad("Pilates en la Playa", "Clases de pilates al aire libre", 40.0f, 20, fechaCelebracion, fechaInicioInscripcion, fechaFinInscripcion);

        List<Actividad> actividadesAbiertas = serviciosAdmin.listarActividadesDisponibles();
        // REFRESCAR socioCarlos HACIENDO UN LOGIN
        socioCarlos = serviciosAdmin.recuperarSocioPorEmail("carlos@example.com").orElseThrow(UsuarioNoRegistrado::new);
        Solicitud solicitud = servicioSocios.echarSolicitud(socioCarlos, actividadesAbiertas.get(0).getId(), 2);

        assertNotNull(solicitud);
        assertEquals(2, solicitud.getNumAcompaniantes());
        assertEquals("Pilates en la Playa", solicitud.getActividad().getTitulo());
    }


    @Test
    @DirtiesContext
    public void testEcharSolicitud_ActividadSinEspacio() {
        // Simulación de login para Carlos
        var socioCarlos = serviciosAdmin.recuperarSocioPorEmail("carlos@example.com").orElseThrow(UsuarioNoRegistrado::new);
        var socioElena = serviciosAdmin.recuperarSocioPorEmail("elena@example.com").orElseThrow(UsuarioNoRegistrado::new);

        socioCarlos.setHaPagado(true);
        socioElena.setHaPagado(true);
        LocalDate fechaCelebracion = LocalDate.now().plusDays(10);
        LocalDate fechaInicioInscripcion = LocalDate.now().minusDays(10);
        LocalDate fechaFinInscripcion = LocalDate.now().plusDays(1);
        serviciosAdmin.crearTemporada();
        var a  = serviciosAdmin.crearActividad("Pilates en la Playa", "Clases de pilates al aire libre", 40.0f, 2, fechaCelebracion, fechaInicioInscripcion, fechaFinInscripcion);

        List<Actividad> actividadesAbiertas = serviciosAdmin.listarActividadesDisponibles();
        // REFRESCAR socioCarlos HACIENDO UN LOGIN
       // var th = new Thread(()-> {
            try {
                Solicitud solicitud = servicioSocios.echarSolicitud(socioCarlos, actividadesAbiertas.get(0).getId(), 2);

            }catch (NumeroDePlazasIncorrecto e){
                Logger.getLogger(servicioSocios.getClass().getName()).warning("echar solicitud imposible maximo plazas alcanzado");
            }
        //});
        //th.start();
        try {
            Solicitud solicitudDOS = servicioSocios.echarSolicitud(socioElena, actividadesAbiertas.get(0).getId(), 2);

        }catch (NumeroDePlazasIncorrecto e){
            Logger.getLogger(servicioSocios.getClass().getName()).warning("echar solicitud 2 imposible maximo plazas alcanzado");
        }
        //try { th.join(); } catch(InterruptedException e) {}
        var act = serviciosAdmin.buscarActividad(actividadesAbiertas.get(0).getId());
        assertThat(act.getSolicitudes().size() == 2);

    }
    @Test
    @DirtiesContext
    public void testEcharSolicitud_ActividadNoExistente() {
        // Simulación de login para Elena
        var socioElena = serviciosAdmin.recuperarSocioPorEmail("elena@example.com").orElseThrow(UsuarioNoRegistrado::new);

        serviciosAdmin.crearTemporada();
        assertThatThrownBy(() -> servicioSocios.echarSolicitud(socioElena, 999, 1)).isInstanceOf(ActividadNoExistente.class);
    }

    @Test
    @DirtiesContext
    public void testModificarSolicitud() {
        // Simulación de login para Raul
        var socioRaul = serviciosAdmin.recuperarSocioPorEmail("raul@example.com").orElseThrow(UsuarioNoRegistrado::new);

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
        var socioLaura = serviciosAdmin.recuperarSocioPorEmail("laura@example.com").orElseThrow(UsuarioNoRegistrado::new);

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
