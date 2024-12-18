package rest;

import es.ujaen.dae.entidades.Socio;
import es.ujaen.dae.rest.dto.DActividad;
import es.ujaen.dae.rest.dto.DSocio;
import es.ujaen.dae.rest.dto.DSolicitud;
import es.ujaen.dae.rest.dto.DTemporada;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = es.ujaen.dae.app.ClubDeSocios.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(profiles = {"test"})
public class TestControladorClub {

    @LocalServerPort
    int localPort;


    TestRestTemplate restTemplate;


    /**
     * TestRestTemplate
     */
    @PostConstruct
    void crearRestTemplate() {
        var restTemplateBuilder = new RestTemplateBuilder().rootUri("http://localhost:" + localPort + "/clubDeSocios");

        restTemplate = new TestRestTemplate(restTemplateBuilder);
    }

    /**
     * crear socio invalido
     */

    @Test
    @DirtiesContext
    public void testNuevoSocioInvalido() {
        var socio = new DSocio(1,"emailgmail.com","Juan","Matias","643611225","1234",false);
        var respuesta = restTemplate.postForEntity(
                "/socios",
                socio,
                Void.class
        );
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        var socio2 = new DSocio(0,"email@gmail.com","Juan","Matias","643611225","1234",false);
        respuesta = restTemplate.postForEntity(
                "/socios",
                socio2,
                Void.class
        );
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);


        respuesta = restTemplate.postForEntity(
                "/socios",
                socio2,
                Void.class
        );
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

    }

    @Test
    @DirtiesContext
    void testLogin() {

        var socio = new DSocio(0,"email@gmail.com","Juan","Matias","652584273","1234",false);
        var respuesta = restTemplate.postForEntity(
                "/socios",
                socio,
                Void.class
        );
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);


        var respuestaLogin = restTemplate.withBasicAuth("aaaa@gmail.com",socio.claveAcceso()).getForEntity(
                "/socios/{email}",
                DSocio.class,
                socio.email()
        );

        assertThat(respuestaLogin.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        respuestaLogin = restTemplate.withBasicAuth(socio.email(), "xyz").getForEntity(
                "/socios/{email}",
                DSocio.class,
                socio.email()
        );

        assertThat(respuestaLogin.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        respuestaLogin = restTemplate.withBasicAuth(socio.email(), socio.claveAcceso()).getForEntity(
                "/socios/{email}",
                DSocio.class,
                socio.email()
        );
        assertThat(respuestaLogin.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuestaLogin.getBody().email()).isEqualTo(socio.email());
    }

    @Test
    @DirtiesContext
    void testCrearTemporada(){
        int anio = LocalDate.now().getYear();
        var temporada = new DTemporada(anio);
        var respuesta = restTemplate.withBasicAuth("admin@admin.com", "adminPassword").postForEntity(
                "/temporadas",
                temporada,
                Void.class
        );
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        anio = 2023;
        temporada = new  DTemporada(anio);
        respuesta = restTemplate.withBasicAuth("admin@admin.com", "adminPassword").postForEntity(
                "/temporadas",
                temporada,
                Void.class
        );
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    @DirtiesContext
    void testObtenerTemporada(){
        int anio = LocalDate.now().getYear();
        var temporada = new DTemporada(anio);
        var respuesta = restTemplate.withBasicAuth("admin@admin.com", "adminPassword").postForEntity(
                "/temporadas",
                temporada,
                Void.class
        );
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var respuesta2 = restTemplate.getForEntity(
                "/temporadas/{anio}",
                DTemporada.class,
                anio
        );

        assertThat(respuesta2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta2.getBody().anio()).isEqualTo(anio);
    }


    @Test
    @DirtiesContext
    void testObtenerTodasLasTemporadas(){
        int anio = LocalDate.now().getYear();
        var temporada = new DTemporada(anio);
        var respuesta = restTemplate.withBasicAuth("admin@admin.com", "adminPassword").postForEntity(
                "/temporadas",
                temporada,
                Void.class
        );

        var respuesta2 = restTemplate.getForEntity(
                "/temporadas",
                DTemporada[].class
        );

        assertThat(respuesta2.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuesta2.getBody().length).isGreaterThan(0);
    }


    @Test
    @DirtiesContext
    void testNuevaActividad(){
        int anio = LocalDate.now().getYear();
        var temporada = new DTemporada(anio);
        var respuesta = restTemplate.withBasicAuth("admin@admin.com", "adminPassword").postForEntity(
                "/temporadas",
                temporada,
                Void.class
        );

        var actividad = new DActividad(0,"Senderismo en la sierra de Cazorla", "Paseo por los principales puntos de la sierra de Cazorla", 50,20,LocalDate.now().plusDays(2),LocalDate.now(),LocalDate.now().plusDays(1),0);
        var respuestaActividad = restTemplate.withBasicAuth("admin@admin.com", "adminPassword").postForEntity(
                "/actividades",
                actividad,
                Void.class
        );

        assertThat(respuestaActividad.getStatusCode()).isEqualTo(HttpStatus.CREATED);


    }


    @Test
    @DirtiesContext
    void testActualizarActividad(){
        int anio = LocalDate.now().getYear();
        var temporada = new DTemporada(anio);
        var respuesta = restTemplate.withBasicAuth("admin@admin.com", "adminPassword").postForEntity(
                "/temporadas",
                temporada,
                Void.class
        );

        var actividad = new DActividad(0,"Senderismo en la sierra de Cazorla", "Paseo por los principales puntos de la sierra de Cazorla", 50,20,LocalDate.now().plusDays(2),LocalDate.now(),LocalDate.now().plusDays(1),0);
        var respuestaActividad = restTemplate.withBasicAuth("admin@admin.com", "adminPassword").postForEntity(
                "/actividades",
                actividad,
                Void.class
        );
        assertThat(respuestaActividad.getStatusCode()).isEqualTo(HttpStatus.CREATED);


        var respuestaActividades = restTemplate.getForEntity(
                "/temporadas/{anio}/actividades",
                DActividad[].class,
                anio
        );

        DActividad[] actividades = respuestaActividades.getBody();
        int idActividadEncontrada = -1;
        if (actividades != null) {
            for (DActividad actividadEnBusqueda : actividades) {
                if (Objects.equals(actividadEnBusqueda.titulo(), actividad.titulo())) {
                    idActividadEncontrada = actividadEnBusqueda.id();
                    break;
                }
            }
        }
        var actividad2 = new DActividad(0,"Senderismo en la sierra de Jaen", "Paseo por los principales puntos de la sierra de Cazorla", 50,20,LocalDate.now().plusDays(2),LocalDate.now(),LocalDate.now().plusDays(1),0);
        var respuestaActividad2 = restTemplate.withBasicAuth("admin@admin.com", "adminPassword").postForEntity(
                "/actividades/{idActividad}",
                actividad2,
                DActividad.class,
                idActividadEncontrada
        );
        assertThat(respuestaActividad2.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);


    }



    @Test
    @DirtiesContext
    void testBuscarActividad(){
        int anio = LocalDate.now().getYear();
        var temporada = new DTemporada(anio);
        var respuesta = restTemplate.withBasicAuth("admin@admin.com", "adminPassword").postForEntity(
                "/temporadas",
                temporada,
                Void.class
        );

        var actividad = new DActividad(0,"Senderismo en la sierra de Cazorla", "Paseo por los principales puntos de la sierra de Cazorla", 50,20,LocalDate.now().plusDays(2),LocalDate.now(),LocalDate.now().plusDays(1),0);
        respuesta = restTemplate.withBasicAuth("admin@admin.com", "adminPassword").postForEntity(
                "/actividades",
                actividad,
                Void.class
        );

        var respuestaActividad = restTemplate.getForEntity(
                "/actividades/{idActividad}",
                DActividad.class,
                1
        );

        assertThat(respuestaActividad.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuestaActividad.getBody().titulo()).isEqualTo(actividad.titulo());

    }

    @Test
    @DirtiesContext
    void testObtenerActividadesTemporada(){
        int anio = LocalDate.now().getYear();
        var temporada = new DTemporada(anio);
        var respuesta = restTemplate.withBasicAuth("admin@admin.com", "adminPassword").postForEntity(
                "/temporadas",
                temporada,
                Void.class
        );

        var actividad = new DActividad(0,"Senderismo en la sierra de Cazorla", "Paseo por los principales puntos de la sierra de Cazorla", 50,20,LocalDate.now().plusDays(2),LocalDate.now(),LocalDate.now().plusDays(1),0);
        respuesta = restTemplate.withBasicAuth("admin@admin.com", "adminPassword").postForEntity(
                "/actividades",
                actividad,
                Void.class
        );

        var actividad2 = new DActividad(0,"Kayak en la playa", "Paseo en kayak por la playa de Tarifa", 50,20,LocalDate.now().plusDays(2),LocalDate.now(),LocalDate.now().plusDays(1),0);
        respuesta = restTemplate.withBasicAuth("admin@admin.com", "adminPassword").postForEntity(
                "/actividades",
                actividad,
                Void.class
        );

        var respuestaActividades = restTemplate.getForEntity(
                "/temporadas/{anio}/actividades",
                DActividad[].class,
                anio
        );

        assertThat(respuestaActividades.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuestaActividades.getBody().length).isEqualTo(2);
    }


    @Test
    @DirtiesContext
    void testCrearSolicitud(){
        int anio = LocalDate.now().getYear();
        var temporada = new DTemporada(anio);
        var respuesta = restTemplate.withBasicAuth("admin@admin.com", "adminPassword").postForEntity(
                "/temporadas",
                temporada,
                Void.class
        );

        var actividad = new DActividad(0,"Senderismo en la sierra de Cazorla", "Paseo por los principales puntos de la sierra de Cazorla", 50,20,LocalDate.now().plusDays(2),LocalDate.now(),LocalDate.now().plusDays(1),0);
        respuesta = restTemplate.withBasicAuth("admin@admin.com", "adminPassword").postForEntity(
                "/actividades",
                actividad,
                Void.class
        );
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var socio = new DSocio(0,"email@gmail.com","Juan","Matias","652584273","1234",false);
        var repuestaSocio = restTemplate.withBasicAuth("admin@admin.com", "adminPassword").postForEntity(
                "/socios",
                socio,
                Void.class
        );
        assertThat(repuestaSocio.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var actividades = restTemplate.getForEntity(
                "/temporadas/{anio}/actividades",
                DActividad[].class,
                anio
        );

        var respuestaBusquedaSocio = restTemplate.withBasicAuth("email@gmail.com","1234").getForEntity(
                "/socios/{email}",
                DSocio.class,
                socio.email(),
                socio.claveAcceso()
        );
        assertThat(respuestaBusquedaSocio.getStatusCode()).isEqualTo(HttpStatus.OK);

         var solicitud = new DSolicitud(0,2,false,0,respuestaBusquedaSocio.getBody().idSocio(),actividades.getBody()[0].id());
         var respuestaSolicitud = restTemplate.withBasicAuth("email@gmail.com","1234").postForEntity(
                 "/solicitudes",
                 solicitud,
                 Void.class
         );

        assertThat(respuestaSolicitud.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    @DirtiesContext
    void testObtenerSolicitud(){
        int anio = LocalDate.now().getYear();
        var temporada = new DTemporada(anio);
        var respuesta = restTemplate.withBasicAuth("admin@admin.com", "adminPassword").postForEntity(
                "/temporadas",
                temporada,
                Void.class
        );
        var actividad = new DActividad(0,"Senderismo en la sierra de Cazorla", "Paseo por los principales puntos de la sierra de Cazorla", 50,20,LocalDate.now().plusDays(2),LocalDate.now(),LocalDate.now().plusDays(1),0);
        respuesta = restTemplate.withBasicAuth("admin@admin.com", "adminPassword").postForEntity(
                "/actividades",
                actividad,
                Void.class
        );
        var socio = new DSocio(0,"email@gmail.com","Juan","Matias","652584273","1234",false);
        var repuestaSocio = restTemplate.postForEntity(
                "/socios",
                socio,
                Void.class
        );
        var respuestaBusquedaSocio = restTemplate.withBasicAuth("email@gmail.com","1234").getForEntity(
                "/socios/{email}",
                DSocio.class,
                socio.email(),
                socio.claveAcceso()
        );
        var actividades = restTemplate.getForEntity(
                "/temporadas/{anio}/actividades",
                DActividad[].class,
                anio
        );
        var solicitud = new DSolicitud(0,2,false,0,respuestaBusquedaSocio.getBody().idSocio(),actividades.getBody()[0].id());
        var respuestaSolicitud = restTemplate.withBasicAuth("email@gmail.com","1234").postForEntity(
                "/solicitudes",
                solicitud,
                Void.class
        );
        var respuestaBusqueda = restTemplate.withBasicAuth("email@gmail.com","1234").getForEntity(
                "/solicitudes/{idSolicitud}",
                DSolicitud.class,
                1
        );
        assertThat(respuestaBusqueda.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuestaBusqueda.getBody().idActividad()).isEqualTo(actividades.getBody()[0].id());
        assertThat(respuestaBusqueda.getBody().idSocio()).isEqualTo(respuestaBusquedaSocio.getBody().idSocio());
    }

    @Test
    @DirtiesContext
    void testObtenerSolicitudesActividad(){
        int anio = LocalDate.now().getYear();
        var temporada = new DTemporada(anio);
        var respuesta = restTemplate.withBasicAuth("admin@admin.com", "adminPassword").postForEntity(
                "/temporadas",
                temporada,
                Void.class
        );

        var actividad = new DActividad(0,"Senderismo en la sierra de Cazorla", "Paseo por los principales puntos de la sierra de Cazorla", 50,20,LocalDate.now().plusDays(2),LocalDate.now(),LocalDate.now().plusDays(1),0);
        respuesta = restTemplate.withBasicAuth("admin@admin.com", "adminPassword").postForEntity(
                "/actividades",
                actividad,
                Void.class
        );
        var socio = new DSocio(0,"email@gmail.com","Juan","Matias","652584273","1234",false);
        var repuestaSocio = restTemplate.postForEntity(
                "/socios",
                socio,
                Void.class
        );
        var respuestaBusquedaSocio = restTemplate.withBasicAuth("email@gmail.com","1234").getForEntity(
                "/socios/{email}",
                DSocio.class,
                socio.email(),
                socio.claveAcceso()
        );
        var actividades = restTemplate.getForEntity(
                "/temporadas/{anio}/actividades",
                DActividad[].class,
                anio
        );
        var solicitud = new DSolicitud(0,2,false,0,respuestaBusquedaSocio.getBody().idSocio(),actividades.getBody()[0].id());
        var respuestaSolicitud = restTemplate.withBasicAuth("email@gmail.com","1234").postForEntity(
                "/solicitudes",
                solicitud,
                Void.class
        );
        var respuestaBusqueda = restTemplate.withBasicAuth("admin@admin.com","adminPassword").getForEntity(
                "/actividades/{idActividad}/solicitudes",
                DSolicitud[].class,
                actividades.getBody()[0].id()
        );
        assertThat(respuestaBusqueda.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuestaBusqueda.getBody()[0].idActividad()).isEqualTo(actividades.getBody()[0].id());
    }

    @Test
    @DirtiesContext
    void testObtenerSolicitudesSocio(){
        int anio = LocalDate.now().getYear();
        var temporada = new DTemporada(anio);
        var respuesta = restTemplate.withBasicAuth("admin@admin.com", "adminPassword").postForEntity(
                "/temporadas",
                temporada,
                Void.class
        );

        var actividad = new DActividad(0,"Senderismo en la sierra de Cazorla", "Paseo por los principales puntos de la sierra de Cazorla", 50,20,LocalDate.now().plusDays(2),LocalDate.now(),LocalDate.now().plusDays(1),0);
        respuesta = restTemplate.withBasicAuth("admin@admin.com", "adminPassword").postForEntity(
                "/actividades",
                actividad,
                Void.class
        );
        var socio = new DSocio(0,"email@gmail.com","Juan","Matias","652584273","1234",false);
        var repuestaSocio = restTemplate.postForEntity(
                "/socios",
                socio,
                Void.class
        );
        var respuestaBusquedaSocio = restTemplate.withBasicAuth("email@gmail.com","1234").getForEntity(
                "/socios/{email}",
                DSocio.class,
                socio.email(),
                socio.claveAcceso()
        );
        var actividades = restTemplate.getForEntity(
                "/temporadas/{anio}/actividades",
                DActividad[].class,
                anio
        );
        var solicitud = new DSolicitud(0,2,false,0,respuestaBusquedaSocio.getBody().idSocio(),actividades.getBody()[0].id());
        var respuestaSolicitud = restTemplate.withBasicAuth("email@gmail.com","1234").postForEntity(
                "/solicitudes",
                solicitud,
                Void.class
        );
        var respuestaBusqueda = restTemplate.withBasicAuth("email@gmail.com","1234").getForEntity(
                "/socios/{email}/solicitudes",
                DSolicitud[].class,
                respuestaBusquedaSocio.getBody().email()
        );
        assertThat(respuestaBusqueda.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuestaBusqueda.getBody()[0].idSocio()).isEqualTo(respuestaBusquedaSocio.getBody().idSocio());
    }

    @Test
    @DirtiesContext
    void testBorrarSolicitud(){
        int anio = LocalDate.now().getYear();
        var temporada = new DTemporada(anio);
        var respuesta = restTemplate.withBasicAuth("admin@admin.com", "adminPassword").postForEntity(
                "/temporadas",
                temporada,
                Void.class
        );

        var actividad = new DActividad(0,"Senderismo en la sierra de Cazorla", "Paseo por los principales puntos de la sierra de Cazorla", 50,20,LocalDate.now().plusDays(2),LocalDate.now(),LocalDate.now().plusDays(1),0);
        respuesta = restTemplate.withBasicAuth("admin@admin.com", "adminPassword").postForEntity(
                "/actividades",
                actividad,
                Void.class
        );
        var socio = new DSocio(0,"email@gmail.com","Juan","Matias","652584273","1234",false);
        var repuestaSocio = restTemplate.postForEntity(
                "/socios",
                socio,
                Void.class
        );
        var respuestaBusquedaSocio = restTemplate.withBasicAuth("email@gmail.com","1234").getForEntity(
                "/socios/{email}",
                DSocio.class,
                socio.email(),
                socio.claveAcceso()
        );
        var actividades = restTemplate.getForEntity(
                "/temporadas/{anio}/actividades",
                DActividad[].class,
                anio
        );
        var solicitud = new DSolicitud(0,2,false,0,respuestaBusquedaSocio.getBody().idSocio(),actividades.getBody()[0].id());
        var respuestaSolicitud = restTemplate.withBasicAuth("email@gmail.com","1234").postForEntity(
                "/solicitudes",
                solicitud,
                Void.class
        );
        var solicitudABorrar = restTemplate.withBasicAuth("email@gmail.com","1234").getForEntity(
                "/socios/{email}/solicitudes",
                DSolicitud[].class,
                respuestaBusquedaSocio.getBody().email()
        ).getBody()[0];
        restTemplate.withBasicAuth("email@gmail.com","1234").exchange(
                "/solicitudes",
                HttpMethod.DELETE,
                new HttpEntity<>(solicitudABorrar),
                DSolicitud.class
                );
        var busquedaPostBorrado = restTemplate.withBasicAuth("email@gmail.com","1234").getForEntity(
                "/solicitudes/{idSolicitud}",
                DSolicitud.class,
                solicitudABorrar.idSolicitud()
        );
        assertThat(busquedaPostBorrado.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DirtiesContext
    void testModificarSolicitud(){
        int anio = LocalDate.now().getYear();
        var temporada = new DTemporada(anio);
        restTemplate.withBasicAuth("admin@admin.com", "adminPassword").postForEntity(
                "/temporadas",
                temporada,
                Void.class
        );
        var actividad = new DActividad(0,"Senderismo en la sierra de Cazorla", "Paseo por los principales puntos de la sierra de Cazorla", 50,20,LocalDate.now().plusDays(2),LocalDate.now(),LocalDate.now().plusDays(1),0);
        restTemplate.withBasicAuth("admin@admin.com", "adminPassword").postForEntity(
                "/actividades",
                actividad,
                Void.class
        );
        var socio = new DSocio(0,"email@gmail.com","Juan","Matias","652584273","1234",false);
        restTemplate.postForEntity(
                "/socios",
                socio,
                Void.class
        );
        var respuestaBusquedaSocio = restTemplate.withBasicAuth("email@gmail.com","1234").getForEntity(
                "/socios/{email}",
                DSocio.class,
                socio.email(),
                socio.claveAcceso()
        );
        var actividades = restTemplate.getForEntity(
                "/temporadas/{anio}/actividades",
                DActividad[].class,
                anio
        );
        var solicitud = new DSolicitud(0,2,false,0,respuestaBusquedaSocio.getBody().idSocio(),actividades.getBody()[0].id());
        restTemplate.withBasicAuth("email@gmail.com","1234").postForEntity(
                "/solicitudes",
                solicitud,
                Void.class
        );
        var respuestaBusquedaSolicitud = restTemplate.withBasicAuth("email@gmail.com","1234").getForEntity(
                "/solicitudes/{idSolicitud}",
                DSolicitud.class,
                1
        );
        var solicitudModificada = new DSolicitud(respuestaBusquedaSolicitud.getBody().idSolicitud(),5,respuestaBusquedaSolicitud.getBody().aceptada(),respuestaBusquedaSolicitud.getBody().acompaniantesAceptados(),respuestaBusquedaSolicitud.getBody().idSocio(),respuestaBusquedaSolicitud.getBody().idActividad());
        restTemplate.withBasicAuth("email@gmail.com","1234").put(
                "/solicitudes",
                solicitudModificada
        );
        var respuestaBusquedaSolicitudActualizada = restTemplate.withBasicAuth("email@gmail.com","1234").getForEntity(
                "/solicitudes/{idSolicitud}",
                DSolicitud.class,
                solicitudModificada.idSolicitud()
        );
        assertThat(respuestaBusquedaSolicitudActualizada.getBody().numAcompaniantes()).isEqualTo(5);
        var solicitudModificadaAdmin = new DSolicitud(respuestaBusquedaSolicitud.getBody().idSolicitud(),5,respuestaBusquedaSolicitud.getBody().aceptada(),3,respuestaBusquedaSolicitud.getBody().idSocio(),respuestaBusquedaSolicitud.getBody().idActividad());

        var actualizarPLazas = restTemplate.withBasicAuth("admin@admin.com","adminPassword").exchange(
                "/solicitudes",
                HttpMethod.PUT,
                new HttpEntity<>(solicitudModificadaAdmin),
                DSolicitud.class
        );
        assertThat(actualizarPLazas.getStatusCode()).isEqualTo(HttpStatus.OK);
        var respuestaBusquedaSolicitudActualizadaAdmin = restTemplate.withBasicAuth("email@gmail.com","1234").getForEntity(
                "/solicitudes/{idSolicitud}",
                DSolicitud.class,
                solicitudModificadaAdmin.idSolicitud()
        );
        assertThat(respuestaBusquedaSolicitudActualizadaAdmin.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuestaBusquedaSolicitudActualizadaAdmin.getBody().acompaniantesAceptados()).isEqualTo(2);
    }


    @Test
    void testPagarCuota() {
        var socio = new DSocio(0, "juan.perez@email.com", "Juan", "Pérez", "678912345", "clave123", false);

        var respuestaCreacionSocio = restTemplate.postForEntity(
                "/socios",
                socio,
                Void.class
        );
        assertThat(respuestaCreacionSocio.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        var respuestaPago = restTemplate.withBasicAuth("juan.perez@email.com", "clave123")
                .exchange(
                        "/clubdesocios/socios/{email}",
                        HttpMethod.PUT,
                        null,
                        DSocio.class,
                        socio.email()
                );
        assertThat(respuestaPago.getStatusCode()).isEqualTo(HttpStatus.OK);

        var respuestaBusquedaSocio = restTemplate.withBasicAuth("juan.perez@email.com", "clave123")
                .getForEntity(
                        "/socios/{email}",
                        DSocio.class,
                        socio.email()
                );

        //Verificar que el estado es 200 OK
        assertThat(respuestaBusquedaSocio.getStatusCode()).isEqualTo(HttpStatus.OK);

        //Acceder al cuerpo de la respuesta
        var socioActualizado = respuestaBusquedaSocio.getBody();
        assertThat(socioActualizado).isNotNull();
        assertThat(socioActualizado.haPagado()).isTrue();

    }
}
