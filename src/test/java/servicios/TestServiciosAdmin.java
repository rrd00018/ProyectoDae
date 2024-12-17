package servicios;

import es.ujaen.dae.entidades.Socio;
import es.ujaen.dae.entidades.Solicitud;
import es.ujaen.dae.excepciones.*;
import es.ujaen.dae.repositorios.RepositorioTemporada;
import es.ujaen.dae.servicios.ServicioSocios;
import es.ujaen.dae.servicios.ServiciosAdmin;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = es.ujaen.dae.app.ClubDeSocios.class)
@Validated
@ActiveProfiles("test")
public class TestServiciosAdmin {
    @Autowired
    ServiciosAdmin serviciosAdmin;

    @Autowired
    ServicioSocios servicioSocios;

    @Test
    @DirtiesContext
    public void testNuevoSocio(){
        Socio s = new Socio(0,"juan@gmail.com", "Juan","Torres","684190546","1234",false);
        var socio = serviciosAdmin.crearSocio(s);
        assertThat(socio).isNotNull();
    }

    @Test
    @DirtiesContext
    public void testNuevoSocioDuplicado(){
        Socio s = new Socio(0,"juan@gmail.com", "Juan","Torres","684190546","1234",false);
        serviciosAdmin.crearSocio(s);
        assertThatThrownBy(() -> serviciosAdmin.crearSocio(s)).isInstanceOf(UsuarioYaRegistrado.class);
    }

    @Test
    @DirtiesContext
    public void testNuevoSocioConFallos(){
        Socio s = new Socio(0,"a","","","1","",false);
        assertThatThrownBy(() -> serviciosAdmin.crearSocio(s)).isInstanceOf(ConstraintViolationException.class);
    }

    @Test
    @DirtiesContext
    public void testCrearActividad() {
        int anioTemporada = LocalDate.now().getYear();
        serviciosAdmin.crearTemporada();

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
        serviciosAdmin.crearTemporada();

        // Datos de la actividad con fechas incorrectas
        String titulo = "Clase";
        String descripcion = "Clase ";
        float precio = 50.0f;
        int plazas = 15;
        LocalDate fechaCelebracion = LocalDate.of(anioTemporada, 5, 15);
        LocalDate fechaInicioInscripcion = LocalDate.of(anioTemporada, 6, 1);
        LocalDate fechaFinInscripcion = LocalDate.of(anioTemporada, 7, 1);

        // Verificación: Debe lanzar una excepción de FechaIncorrecta
        assertThatThrownBy(() ->
                serviciosAdmin.crearActividad(titulo, descripcion, precio, plazas, fechaCelebracion, fechaInicioInscripcion, fechaFinInscripcion)
        ).isInstanceOf(FechaIncorrecta.class);
    }


    @Test
    @DirtiesContext
    public void testCrearTemporadaExistente(){
        serviciosAdmin.crearTemporada();
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
        serviciosAdmin.crearTemporada();
        var actividad = serviciosAdmin.crearActividad("Clase de yoga","Clase de yoga al aire libre",50,5,LocalDate.now().plusDays(10),LocalDate.now().minusDays(5),LocalDate.now().plusDays(1));
        Socio carlos = new Socio(0,"carlos@example.com", "Carlos", "Perez", "600000001", "claveCarlos123",false);
        Socio elena = new Socio(0,"elena@example.com", "Elena", "Gomez", "600000002", "claveElena456",false);
        Socio raul = new Socio(0,"raul@example.com", "Raul", "Martinez", "600000003", "claveRaul789",false);
        var usuario1 = serviciosAdmin.crearSocio(carlos);
        var usuario2 = serviciosAdmin.crearSocio(elena);
        var usuario3 = serviciosAdmin.crearSocio(raul);

        serviciosAdmin.pagar(usuario1);
        serviciosAdmin.pagar(usuario3);

        var actividades = serviciosAdmin.listarActividadesDisponibles();

        servicioSocios.echarSolicitud(usuario1, actividades.get(0).getId(), 2);
        servicioSocios.echarSolicitud(usuario2, actividades.get(0).getId(),5);
        servicioSocios.echarSolicitud(usuario3, actividades.get(0).getId(),4);

        actividades.get(0).setFechaFinInscripcion(LocalDate.now().minusDays(1));
        serviciosAdmin.actualizarActividad(actividades.get(0));
        serviciosAdmin.cerrarActividad(actividades.get(0).getId());

        actividad = serviciosAdmin.buscarActividad(actividad.getId());

        List<Solicitud> solicitudes = serviciosAdmin.listarSolicitudesActividad(actividades.get(0));

        int plazasTotales = 0;
        for (Solicitud s : solicitudes){
            plazasTotales += s.getAcompaniantesAceptados() + (s.isAceptada() ? 1 : 0);
        }

        assertThat(plazasTotales).isEqualTo(actividad.getPlazasAsignadas());
    }

    @Test
    @DirtiesContext

    public void testCerrarActividadManualmente() {
        serviciosAdmin.crearTemporada();
        var actividad = serviciosAdmin.crearActividad("Clase de yoga", "Clase de yoga al aire libre", 50, 10, LocalDate.now().plusDays(10), LocalDate.now().minusDays(5), LocalDate.now().plusDays(1));
        Socio carlos = new Socio(0,"carlos@example.com", "Carlos", "Perez", "600000001", "claveCarlos123",false);
        Socio elena = new Socio(0,"elena@example.com", "Elena", "Gomez", "600000002", "claveElena456",false);
        Socio raul = new Socio(0,"raul@example.com", "Raul", "Martinez", "600000003", "claveRaul789",false);
        var usuario1 = serviciosAdmin.crearSocio(carlos);
        var usuario2 = serviciosAdmin.crearSocio(elena);
        var usuario3 = serviciosAdmin.crearSocio(raul);

        serviciosAdmin.pagar(usuario1);
        serviciosAdmin.pagar(usuario3);

        var actividades = serviciosAdmin.listarActividadesDisponibles();

        servicioSocios.echarSolicitud(usuario1, actividades.get(0).getId(), 2);
        servicioSocios.echarSolicitud(usuario2, actividades.get(0).getId(), 5);
        servicioSocios.echarSolicitud(usuario3, actividades.get(0).getId(), 5);

        actividades.get(0).setFechaFinInscripcion(LocalDate.now().minusDays(1));
        serviciosAdmin.actualizarActividad(actividades.get(0));

        var solicitudes = serviciosAdmin.listarSolicitudesActividad(actividades.get(0));

        serviciosAdmin.procesarSolicitudManualmente(solicitudes.get(2), 5);
        serviciosAdmin.procesarSolicitudManualmente(solicitudes.get(0), 2);

        actividad = serviciosAdmin.buscarActividad(actividad.getId());

        int plazasTotales = serviciosAdmin.listarSolicitudesActividad(actividad).stream().mapToInt(solicitud -> solicitud.getAcompaniantesAceptados() + (solicitud.isAceptada() ? 1 : 0)).sum();

        assertThat(plazasTotales).isEqualTo(actividad.getPlazasAsignadas());

    }
}