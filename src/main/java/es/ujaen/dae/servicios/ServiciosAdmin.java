package es.ujaen.dae.servicios;

import es.ujaen.dae.entidades.Actividad;
import es.ujaen.dae.entidades.Socio;
import es.ujaen.dae.entidades.Temporada;
import es.ujaen.dae.excepciones.ClienteRegistrado;
import es.ujaen.dae.excepciones.FechaIncorrecta;
import es.ujaen.dae.excepciones.FechaNoAlcanzada;
import es.ujaen.dae.excepciones.TemporadaNoExiste;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

    /**
     * @brief Crea un socio nuevo con todos sus datos
     * @param email
     * @param nombre
     * @param apellidos
     * @param telefono
     * @param claveAcceso
     * @return
     * @throws Exception
     */
    public Socio crearSocio(String email, String nombre, String apellidos, int telefono, String claveAcceso) {
        if(socios.containsKey(email))
            throw new ClienteRegistrado();
        else{
            Socio s = new Socio(email,nombre,apellidos,telefono,claveAcceso);
            socios.put(email,s);
            return s;
        }
    }

    public Actividad crearActividad(Temporada temporada, String titulo, String descripcion, float precio, int plazas, LocalDate fechaCelebracion, LocalDate fechaInicioInscripcion, LocalDate fechaFinInscripcion) {
        if(temporadas.containsValue(temporada)){
            if(fechaCelebracion.isAfter(fechaInicioInscripcion) && fechaCelebracion.isAfter(fechaFinInscripcion)) {
                if(fechaInicioInscripcion.isBefore(fechaFinInscripcion)){
                    Actividad actividad = new Actividad(titulo,descripcion,precio,plazas,fechaCelebracion,fechaInicioInscripcion,fechaFinInscripcion,temporada);
                    temporadas.get(fechaCelebracion.getYear()).crearActividad(actividad);
                    return actividad;
                }else throw new FechaNoAlcanzada();
            }else throw new FechaNoAlcanzada();
        }else throw new TemporadaNoExiste();
    }

    /**
     * @brief Crea una nueva temporada e inicializa el campo pagado de todos los socios a false
     */
    public Temporada crearTemporada(int anio){
        if(LocalDate.now().getYear() <= anio) {
            Temporada t = new Temporada(anio);
            temporadas.put(anio, t);
            for (Socio s : socios.values()) {
                s.setHaPagado(false);
            }
            return temporadas.get(anio);
        }else return null;
    }

    public void cerrarActividad(Temporada temporada, int idActividad) {
        Actividad actividad = temporada.buscarActividad(idActividad);
        if(actividad.getFechaFinInscripcion().isAfter(LocalDate.now())){
            if(actividad.getPlazas() < actividad.getNumPlazasAsignadas()){
                int plazasDisponibles = actividad.getPlazas() - actividad.getNumPlazasAsignadas();
                actividad.moverListaEspera(plazasDisponibles);
            }
        }else throw new FechaNoAlcanzada();
    }

    /**
     * @brief comprueba si una actividad existe en su temporada
     * @param idActividad
     * @return true o false segun la consulta
     */
    public Actividad buscarActividad(int idActividad){
        int temporada = idActividad / 1000;
        return temporadas.get(temporada).buscarActividad(idActividad);
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
}
