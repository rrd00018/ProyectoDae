package servicios;

import java.time.LocalDate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import es.ujaen.dae.servicios.ServiciosAdmin;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

@SpringBootTest(classes = ServiciosAdmin.class)
public class TestServiciosAdmin {
    @Autowired
    ServiciosAdmin serviciosAdmin;

    @Test
    @DirtiesContext
    public void testNuevoSocio(){
        var socio = serviciosAdmin.crearSocio("asdf","Juan","Torres",45,"1234");

    }
}
