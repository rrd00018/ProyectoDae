package es.ujaen.dae.servicios;

import es.ujaen.dae.entidades.Actividad;
import es.ujaen.dae.entidades.Socio;
import es.ujaen.dae.entidades.Temporada;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

@Service
public class ServiciosAdmin {
    private HashMap<String,Socio> socios; //Usa el email como clave
    private HashMap<Integer, Temporada> temporadas; //Usa el año como clave

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
                    Actividad actividad = new Actividad(titulo, descripcion, precio, plazas, fechaCelebracion, fechaInicioInscripcion, fechaFinInscripcion, temporada);
                    temporada.crearActividad(actividad);
                    return actividad;
                }else throw new Exception("La fecha de inicio de inscripcion debe ser anterior a la de fin de inscripcion");
            }else throw new Exception("La fecha de inicio de la inscripcion y de fin de la inscripcion deben ser previas a la celebracion de la actividad");
        }else throw new Exception("La temporada no existe");
    }

    /**
     * @brief Devuelve el objeto socio para logearse
     * @param email email del socio
     * @param clave clave de acceso del socio
     * @return Optional.empty si el login es correcto o Optional.of(Socio) si existe
     */
    public Optional<Socio> login(String email, String clave){
        Socio s = socios.get(email);
        if(s != null){
            if(s.getClaveAcceso().equals(clave))
                return Optional.of(s);
        }
        return Optional.empty();
    }

    public Temporada crearTemporada(Integer anio){
        Temporada t = new Temporada(anio);
        temporadas.put(anio,t);
        return t;
    }


    public void cerrarActividad(Temporada temporada, Integer idActividad){

    }

    /**
     * @brief comprueba si una actividad existe en su temporada
     * @param idActividad
     * @return true o false segun la consulta
     */
    public Actividad buscarActividad( Integer idActividad){
        Integer temporada = idActividad / 1000;
        return temporadas.get(temporada).buscarActividad(idActividad);
    }
}
