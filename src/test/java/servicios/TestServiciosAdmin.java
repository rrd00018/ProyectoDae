package servicios;

import es.ujaen.dae.entidades.Temporada;
import es.ujaen.dae.excepciones.*;
import es.ujaen.dae.servicios.ServicioSocios;
import es.ujaen.dae.servicios.ServiciosAdmin;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = es.ujaen.dae.app.ClubDeSocios.class)
@Validated
public class TestServiciosAdmin {
    @Autowired
    ServiciosAdmin serviciosAdmin;

    @Autowired
    ServicioSocios servicioSocios;

    @Test
    @DirtiesContext
    public void testNuevoSocio(){
        var socio = serviciosAdmin.crearSocio("juan@gmail.com", "Juan","Torres","684190546","1234");
        assertThat(socio).isNotNull();
    }

    @Test
    @DirtiesContext
    public void testNuevoSocioDuplicado(){
        var socio = serviciosAdmin.crearSocio("juan@gmail.com","Juan","Torres","684190546","1234");
        assertThatThrownBy(() -> serviciosAdmin.crearSocio("juan@gmail.com","Juan","Torres","684190546","1234")).isInstanceOf(ClienteRegistrado.class);
    }

    @Test
    @DirtiesContext
    public void testNuevoSocioConFallos(){
        assertThatThrownBy(() -> serviciosAdmin.crearSocio("a","","","1","")).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DirtiesContext
    public void testCrearActividad() {
        int anioTemporada = LocalDate.now().getYear();
        Temporada temporada = serviciosAdmin.crearTemporada();

        String titulo = "Yoga";
        String descripcion = "Clase";
        float precio = 30;
        int plazas = 20;
        LocalDate fechaCelebracion = LocalDate.of(anioTemporada, 10, 15);
        LocalDate fechaInicioInscripcion = LocalDate.of(anioTemporada, 7, 1);
        LocalDate fechaFinInscripcion = LocalDate.of(anioTemporada, 9, 30);

        // Test: Crear la actividad y verificar que se haya registrado sin errores
        var actividad = serviciosAdmin.crearActividad(titulo, descripcion, precio, plazas, fechaCelebracion, fechaInicioInscripcion, fechaFinInscripcion);

        // Verificación: Buscar la actividad en la temporada
        var actividad2 = serviciosAdmin.buscarActividad(actividad.getId());

        assertThat(actividad).isNotNull();
        assertThat(actividad.getTitulo()).isEqualTo(titulo);
        assertThat(actividad.getDescripcion()).isEqualTo(descripcion);
        assertThat(actividad.getPrecio()).isEqualTo(precio);
        assertThat(actividad2).isNotNull();
    }

    @Test
    @DirtiesContext
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
        ).isInstanceOf(FechaIncorrecta.class);
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


    @Test
    @DirtiesContext
    public void testCerrarActividad(){
        var temporada = serviciosAdmin.crearTemporada();
        var actividad = serviciosAdmin.crearActividad("Clase de yoga","Clase de yoga al aire libre",50,5,LocalDate.now().plusDays(10),LocalDate.now().minusDays(5),LocalDate.now().plusDays(1));
        var usuario1 = serviciosAdmin.crearSocio("paco@gmail.com","Paco","Ruiz Lopez","684190546","1234");
        var usuario2 = serviciosAdmin.crearSocio("juan@gmail.com","Juan","Torres","658986256","1234");
        var usuario3 = serviciosAdmin.crearSocio("maria@example.com", "Maria", "Garcia", "658986258", "claveMaria123");

        serviciosAdmin.pagar(usuario1);
        serviciosAdmin.pagar(usuario3);

        var actividades = serviciosAdmin.listarActividadesDisponibles();

        servicioSocios.echarSolicitud(usuario1, actividades.get(0).getId(), 2);
        servicioSocios.echarSolicitud(usuario2, actividades.get(0).getId(),5);
        servicioSocios.echarSolicitud(usuario3, actividades.get(0).getId(),4);

        actividades.get(0).setFechaFinInscripcion(LocalDate.now().minusDays(1));
        serviciosAdmin.cerrarActividad(actividades.get(0).getId());

        assertThat(usuario1.obtenerSolicitud(actividades.get(0).getId()).getAcompaniantesAceptados() + 1 + usuario2.obtenerSolicitud(actividades.get(0).getId()).getAcompaniantesAceptados() + 1 + usuario3.obtenerSolicitud(actividades.get(0).getId()).getAcompaniantesAceptados() + 1).isBetween(1,actividad.getPlazas());
    }

    @Test
    @DirtiesContext
    public void testCerrarActividadManualmente(){
        serviciosAdmin.crearTemporada();
        var actividad = serviciosAdmin.crearActividad("Clase de yoga","Clase de yoga al aire libre",50,10,LocalDate.now().plusDays(10),LocalDate.now().minusDays(5),LocalDate.now().plusDays(1));
        var usuario1 = serviciosAdmin.crearSocio("paco@gmail.com","Paco","Ruiz Lopez","684190546","1234");
        var usuario2 = serviciosAdmin.crearSocio("juan@gmail.com","Juan","Torres","658986256","1234");
        var usuario3 = serviciosAdmin.crearSocio("maria@example.com", "Maria", "Garcia", "658986258", "claveMaria123");

        serviciosAdmin.pagar(usuario1);
        serviciosAdmin.pagar(usuario3);

        var actividades = serviciosAdmin.listarActividadesDisponibles();

        servicioSocios.echarSolicitud(usuario1, actividades.get(0).getId(), 2);
        servicioSocios.echarSolicitud(usuario2, actividades.get(0).getId(),5);
        servicioSocios.echarSolicitud(usuario3, actividades.get(0).getId(),5);

        actividades.get(0).setFechaFinInscripcion(LocalDate.now().minusDays(1));

        var solicitudes = serviciosAdmin.listarSolicitudesActividad(actividades.get(0));

        serviciosAdmin.procesarSolicitudManualmente(solicitudes.get(2),3);
        serviciosAdmin.procesarSolicitudManualmente(solicitudes.get(1),2);

        assertEquals(4,usuario3.obtenerSolicitud(actividades.get(0).getId()).getAcompaniantesAceptados() + usuario2.obtenerSolicitud(actividades.get(0).getId()).getAcompaniantesAceptados());

        assertThatThrownBy(() ->serviciosAdmin.procesarSolicitudManualmente(solicitudes.get(0),-5)).isInstanceOf(NumeroDePlazasIncorrecto.class);
    }
}
