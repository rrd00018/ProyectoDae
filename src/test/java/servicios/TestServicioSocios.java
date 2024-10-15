package servicios;

import es.ujaen.dae.entidades.Actividad;
import es.ujaen.dae.entidades.Solicitud;
import es.ujaen.dae.entidades.Socio;
import es.ujaen.dae.servicios.ServicioSocios;
import es.ujaen.dae.servicios.ServiciosAdmin;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDate;
import java.util.ArrayList;

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
        Socio socio = new Socio("pedro@example.com", "Pedro", "Lopez", 987654321, "clavePedro456");

        Solicitud solicitud = servicioSocios.echarSolicitud(socio, 999, 2);

        assertNull(solicitud);
    }

    @Test
    @DirtiesContext
    public void testModificarSolicitud() {
        Socio socio = new Socio("ana@example.com", "Ana", "Martinez", 555555555, "claveAna789");
        LocalDate fechaCelebracion = LocalDate.of(2024, 6, 10);
        LocalDate fechaInicioInscripcion = LocalDate.of(2024, 4, 5);
        LocalDate fechaFinInscripcion = LocalDate.of(2024, 6, 1);
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
    public void testCancelarSolicitud() {
        Socio socio = new Socio("luis@example.com", "Luis", "Ramirez", 111222333, "claveLuis321");
        LocalDate fechaCelebracion = LocalDate.of(2024, 7, 25);
        LocalDate fechaInicioInscripcion = LocalDate.of(2024, 5, 15);
        LocalDate fechaFinInscripcion = LocalDate.of(2024, 7, 20);
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

    @Test
    @DirtiesContext
    public void testCerrarActividad(){
        var temporada = servicioAdmin.crearTemporada();
        var actividad = servicioAdmin.crearActividad("Clase de yoga","Clase de yoga al aire libre",50,10,LocalDate.of(2024,10,20),LocalDate.of(2024,10,9),LocalDate.of(2024,10,14));
        var usuario1 = servicioAdmin.crearSocio("paco@gmail.com","Paco","Ruiz Lopez",684190546,"1234");
        var usuario2 = servicioAdmin.crearSocio("juan@gmail.com","Juan","Torres",658986256,"1234");
        var usuario3 = servicioAdmin.crearSocio("maria@example.com", "Maria", "Garcia", 123456789, "claveMaria123");

        servicioAdmin.pagar(usuario1);
        servicioAdmin.pagar(usuario3);

        var actividades = servicioAdmin.listarActividadesDisponibles();

        servicioSocios.echarSolicitud(usuario1, actividades.get(0).getId(), 2);
        servicioSocios.echarSolicitud(usuario2, actividades.get(0).getId(),5);
        servicioSocios.echarSolicitud(usuario3, actividades.get(0).getId(),4);

        servicioAdmin.cerrarActividad(actividades.get(0).getId());

    }
}
