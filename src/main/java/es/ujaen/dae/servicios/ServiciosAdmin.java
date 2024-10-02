package es.ujaen.dae.servicios;

import es.ujaen.dae.entidades.Actividad;
import es.ujaen.dae.entidades.Socio;
import es.ujaen.dae.entidades.Temporada;
import org.springframework.stereotype.Service;

import java.io.InvalidObjectException;
import java.util.Date;
import java.util.HashMap;

@Service
public class ServiciosAdmin {
    private HashMap<String,Socio> socios;
    private HashMap<Integer, Temporada> temporadas;

    public ServiciosAdmin(){
        socios = new HashMap<>();
        temporadas = new HashMap<>();
    }

    public Socio crearSocio(String email, String nombre, String apellidos, Integer telefono, String claveAcceso) throws Exception {
        if(socios.containsKey(email))
            throw new Exception("Cliente ya registrado");
        else{
            Socio s = new Socio(email,nombre,apellidos,telefono,claveAcceso);
            socios.put(email,s);
            return s;
        }
    }

    public Actividad crearActividad(Temporada temporada, String titulo, String descripcion, Float precio, Integer plazas, Date fechaCelebracion, Date fechaInicioInscripcion, Date fechaFinInscripcion) throws Exception {
        if(temporadas.containsValue(temporada)){
            if(fechaCelebracion.before(fechaInicioInscripcion) && fechaCelebracion.before(fechaFinInscripcion)) {
                if(fechaInicioInscripcion.before(fechaFinInscripcion)){
                    Actividad actividad = new Actividad(titulo, descripcion, precio, plazas, fechaCelebracion, fechaInicioInscripcion, fechaFinInscripcion);
                    temporada.crearActividad(actividad);
                    return actividad;
                }else throw new Exception("La fecha de inicio de inscripcion debe ser anterior a la de fin de inscripcion");
            }else throw new Exception("La fecha de inicio de la inscripcion y de fin de la inscripcion deben ser previas a la celebracion de la actividad");
        }else throw new Exception("La temporada no existe");
    }



}
