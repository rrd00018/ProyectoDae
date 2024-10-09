package servicios;
import es.ujaen.dae.servicios.ServicioSocios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(classes = es.ujaen.dae.app.ClubDeSocios.class)
public class TestServicioSocios {
    @Autowired
    ServicioSocios servicio;



}