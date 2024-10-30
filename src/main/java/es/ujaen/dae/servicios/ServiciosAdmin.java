package es.ujaen.dae.servicios;

import es.ujaen.dae.entidades.Actividad;
import es.ujaen.dae.entidades.Socio;
import es.ujaen.dae.entidades.Solicitud;
import es.ujaen.dae.entidades.Temporada;
import es.ujaen.dae.excepciones.*;
import es.ujaen.dae.repositorios.RepositorioActividad;
import es.ujaen.dae.repositorios.RepositorioSocio;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class ServiciosAdmin {
    @Autowired
    private RepositorioSocio socios; //Usa el email como clave
    @Autowired
    private RepositorioActividad repositorioActividad;

    //private HashMap<String,Socio> socios; //Usa el email como clave
    private HashMap<Integer, Temporada> temporadas; //Usa el año como clave

    public ServiciosAdmin(){
    }


    /**
     * @brief REGISTRA A UN NUEVO SOCIO
     */
    public Socio crearSocio(@Email @NotBlank String email, @NotBlank String nombre, @NotBlank String apellidos, @NotBlank @Pattern(regexp="^(\\+34|0034|34)?[6789]\\d{8}$") String telefono, @NotBlank String claveAcceso) {
        if(socios.existePorEmail(email))
            throw new ClienteRegistrado();
        else{
            Socio s = new Socio(email,nombre,apellidos,telefono,claveAcceso);
            socios.guardar(s);
            return s;
        }
    }


    /**
     * @brief CREA UNA NUEVA ACTIVIDAD
     */
    public Actividad crearActividad(String titulo, String descripcion, float precio, int plazas, LocalDate fechaCelebracion, LocalDate fechaInicioInscripcion, LocalDate fechaFinInscripcion) {
       /** if(temporadas.containsKey(LocalDate.now().getYear())){
            Actividad actividad = new Actividad(titulo,descripcion,precio,plazas,fechaCelebracion,fechaInicioInscripcion,fechaFinInscripcion);
            temporadas.get(fechaCelebracion.getYear()).crearActividad(actividad);
            return actividad;
        }else throw new TemporadaNoExiste();*/
       if(temporadas.containsKey(LocalDate.now().getYear())){
           Actividad actividad = new Actividad(titulo,descripcion,precio,plazas,fechaCelebracion,fechaInicioInscripcion,fechaFinInscripcion);
           temporadas.get(fechaCelebracion.getYear()).crearActividad(actividad);
           repositorioActividad.guardar(actividad);
           return actividad; //TODO Comprobar que la actividad esta bien devuelta
       }else throw new TemporadaNoExiste();
    }


    /**
     * @brief CREA NUEVA TEMPORADA Y ASIGNA NO-PAGADO A TODOS LOS SOCIOS
     */
    public Temporada crearTemporada(){
        if(!temporadas.containsKey(LocalDate.now().getYear())){
            Temporada t = new Temporada(LocalDate.now().getYear());
            temporadas.put(LocalDate.now().getYear(),t);

            for(Socio s : socios.getSocios()){
                s.setHaPagado(false);
            }

            return temporadas.get(LocalDate.now().getYear());
        }else throw new TemporadaYaCreada();
    }


    /**
     * @brief ASIGNA LAS PLAZAS DE IDACTIVIDAD AUTOMATICAMENTE
     * @param idActividad
     */
    public void cerrarActividad(int idActividad){
        Actividad a = temporadas.get(LocalDate.now().getYear()).buscarActividad(idActividad);
        if(a.getFechaFinInscripcion().isBefore(LocalDate.now())) {
            a.asignarAutoJusto();
        }else throw new FechaNoAlcanzada();
    }


    /**
     * @brief DEVUELVE UNA ACTIVIDAD DADO SU ID SI SE ENCUENTRA O NULL
     * @return true o false segun la consulta
     */
    public Actividad buscarActividad(int idActividad){
        /**
        Actividad a = temporadas.get(LocalDate.now().getYear()).buscarActividad(idActividad);
        if(a == null) throw new ActividadNoExistente();
        else return a;
         */
        Optional<Actividad> a = repositorioActividad.buscar(idActividad);
        if(a.isPresent()){
            return a.get();
        }else throw new ActividadNoExistente();
    }


    /**
     * @brief Devuelve el objeto socio para logearse
     * @param email email del socio
     * @param clave clave de acceso del socio
     * @return Optional.empty si el login es correcto o Optional.of(Socio) si existe
     */
    public Optional<Socio> login(String email, String clave) {
        Optional<Socio> s = socios.buscarPorEmail(email);
        if (s.isPresent() && s.get().getClaveAcceso().equals(clave)) {
            return s;
        }
        return Optional.empty();
    }


    /**
     * @brief LISTA LAS ACTIVIDADES DISPONIBLES DE LA TEMPORADA ACTUAL
     * @return arraylist con las actividades
     */
    public ArrayList<Actividad> listarActividadesDisponibles(){
        return temporadas.get(LocalDate.now().getYear()).listarActividadesEnCurso();
    }


    /**
     * @brief REGISTRA QUE UN SOCIO HA PAGADO SU CUOTA
     */
    public void pagar(@Valid Socio socio){
        socio.setHaPagado(true);
    }


    /**
     * Procesa una solicitud manualmente
     * @param s soliciutd a procesar
     */
    public void procesarSolicitudManualmente(@Valid Solicitud s, int nPlazas){
        s.getActividad().procesarSolicitudManualmente(s, nPlazas);
    }


    /**
     * @brief LISTA LAS SOLICITUDES DE UNA ACTIVIDAD
     * (para que el administrador proceda con la asignacion manual)
     */
    public List<Solicitud> listarSolicitudesActividad(Actividad a){
        if(temporadas.get(LocalDate.now().getYear()).buscarActividad(a.getId()) != null){
            return a.getSolicitudes();
        }else throw new ActividadNoExistente();
    }
}
