package servicios;

import es.ujaen.dae.entidades.Actividad;
import es.ujaen.dae.entidades.Solicitud;
import es.ujaen.dae.entidades.Socio;
import es.ujaen.dae.excepciones.ActividadNoExistente;
import es.ujaen.dae.servicios.ServicioSocios;
import es.ujaen.dae.servicios.ServiciosAdmin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = es.ujaen.dae.app.ClubDeSocios.class)
public class TestServicioSocios {

    @Autowired
    private ServiciosAdmin servicioAdmin;

    @Autowired
    private ServicioSocios servicioSocios;

    @Test
    @DirtiesContext
    public void testEcharSolicitud_ActividadExistente() {
        Socio socio = new Socio("maria@example.com", "Maria", "Garcia", "687589120", "claveMaria123");
        LocalDate fechaCelebracion = LocalDate.now().plusDays(10);
        LocalDate fechaInicioInscripcion = LocalDate.now().minusDays(10);
        LocalDate fechaFinInscripcion = LocalDate.now().plusDays(1);
        servicioAdmin.crearTemporada();
        servicioAdmin.crearActividad( "Yoga en el Parque", "Clases de yoga al aire libre", 30.0f, 15,
                fechaCelebracion, fechaInicioInscripcion, fechaFinInscripcion);

        ArrayList<Actividad> actividadesAbiertas = servicioAdmin.listarActividadesDisponibles();

        Solicitud solicitud = servicioSocios.echarSolicitud(socio, actividadesAbiertas.get(0).getId(), 3);

        assertNotNull(solicitud);
        assertEquals(3, solicitud.getNumAcompaniantes());
        assertEquals("Yoga en el Parque", solicitud.getActividad().getTitulo());
    }

    @Test
    @DirtiesContext
    public void testEcharSolicitud_ActividadNoExistente() {
        Socio socio = new Socio("pedro@example.com", "Pedro", "Lopez", "987654321", "clavePedro456");
        var temporada = servicioAdmin.crearTemporada();
        assertThatThrownBy(() -> servicioSocios.echarSolicitud(socio, 999, 2)).isInstanceOf(ActividadNoExistente.class);
    }

    @Test
    @DirtiesContext
    public void testModificarSolicitud() {
        Socio socio = new Socio("ana@example.com", "Ana", "Martinez", "965566235", "claveAna789");
        LocalDate fechaCelebracion = LocalDate.now().plusDays(10);
        LocalDate fechaInicioInscripcion = LocalDate.now().minusDays(10);
        LocalDate fechaFinInscripcion = LocalDate.now().plusDays(1);
        servicioAdmin.crearTemporada();
        servicioAdmin.crearActividad( "Curso de Fotografía", "Aprende a manejar tu cámara", 100.0f, 10,
                fechaCelebracion, fechaInicioInscripcion, fechaFinInscripcion);
        //mira las abiertas
        ArrayList<Actividad> actividadesAbiertas = servicioAdmin.listarActividadesDisponibles();
        //echa solicitud
        servicioSocios.echarSolicitud(socio, actividadesAbiertas.get(0).getId(), 1);
        //listar solicitudes
        ArrayList<Solicitud> solicitudesSocio = servicioSocios.obtenerSolicitudes(socio);
        //modificar solicitudes
        Solicitud solicitudModificada = servicioSocios.modificarSolicitud(socio, solicitudesSocio.get(0).getIdActividad(), 2);

        assertNotNull(solicitudModificada);
        assertEquals(2, solicitudModificada.getNumAcompaniantes());
    }

    @Test
    @DirtiesContext
    public void testCancelarSolicitud() {
        Socio socio = new Socio("luis@example.com", "Luis", "Ramirez", "853969696", "claveLuis321");
        LocalDate fechaCelebracion = LocalDate.now().plusDays(10);
        LocalDate fechaInicioInscripcion = LocalDate.now().minusDays(10);
        LocalDate fechaFinInscripcion = LocalDate.now().plusDays(1);
        servicioAdmin.crearTemporada();
        servicioAdmin.crearActividad( "Taller de Cocina", "Aprende recetas tradicionales", 75.0f, 12,
                fechaCelebracion, fechaInicioInscripcion, fechaFinInscripcion);
        //mira las abiertas
        ArrayList<Actividad> actividadesAbiertas = servicioAdmin.listarActividadesDisponibles();
        //echa solicitud
        servicioSocios.echarSolicitud(socio, actividadesAbiertas.get(0).getId(), 1);
        //listar solicitudes
        ArrayList<Solicitud> solicitudesSocio = servicioSocios.obtenerSolicitudes(socio);
        //eliminar solicitud
        Solicitud solicitudCancelada = servicioSocios.cancelarSolicitud(socio, solicitudesSocio.get(0).getIdActividad());

        assertNotNull(solicitudCancelada);
    }


}
