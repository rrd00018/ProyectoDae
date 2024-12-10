package rest;

import es.ujaen.dae.entidades.Socio;
import es.ujaen.dae.rest.dto.DSocio;
import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

//TODO AHORA MISMO ESTA EN RANDOM PORT, HAY QUE CAPTURARLO Y LUEGO QUEDARSELO
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
        var restTemplateBuilder = new RestTemplateBuilder().rootUri("http://localhost:" + localPort + "/club");  // definir el "/club" correctamente para la web

        restTemplate = new TestRestTemplate(restTemplateBuilder);
    }

    /**
     * crear socio invalido
     */

    @Test
    public void testNuevoSocioInvalido() {
        //el mail es incorrecto por lo cual tiene que dar error
        var socio = new Socio("emailgmail.com","Juan","Matias","611225","1234");
        var respuesta = restTemplate.postForEntity(
                "/socios",
                socio,
                Void.class
        );
        //buscamos que el
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        //buscamos crear un socio correctamente
        var socio2 = new Socio("email@gmail.com","Juan","Matias","611225","1234");
        respuesta = restTemplate.postForEntity(
                "/socios",
                socio2,
                Void.class
        );
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);


        //buscamos crear socio duplicado y que se cree un conflico
        respuesta = restTemplate.postForEntity(
                "/usuarios",
                socio2,
                Void.class
        );
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

    }

    @Test
    void tesLogin() {

        //Creamos el socio
        var socio = new Socio("email@gmail.com","Juan","Matias","611225","1234");
        var respuesta = restTemplate.postForEntity(
                "/socios",
                socio,
                Void.class
        );
        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CREATED);


        //Buscamos no iniciar sesion ya que introducimos el socio que no es pero si la contraseña

        var respuestaLogin = restTemplate.getForEntity(
                "/socios/{email}?clave={clave}",
                DSocio.class,
                "otroemail@gmail.com",
                socio.getClaveAcceso()
        );

        assertThat(respuestaLogin.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        //Buscamos no iniciar sesion ya que introducimos la contraseña que no es pero si el usuario
        respuestaLogin = restTemplate.getForEntity(
                "/usuarios/{email}?clave={clave}",
                DSocio.class,
                socio.getEmail(),
                "contraQueNoEs"
        );
        assertThat(respuestaLogin.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);


        //buscamos iniciar sesion correctamente
        respuestaLogin = restTemplate.getForEntity(
                "/usuarios/{email}?clave={clave}",
                DSocio.class,
                socio.getEmail(),
                socio.getClaveAcceso()
        );
        assertThat(respuestaLogin.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(respuestaLogin.getBody().email()).isEqualTo(socio.getEmail());


    }





}
