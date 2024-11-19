package es.ujaen.dae.servicios;

import es.ujaen.dae.entidades.Actividad;
import es.ujaen.dae.entidades.Socio;
import es.ujaen.dae.entidades.Solicitud;
import es.ujaen.dae.entidades.Temporada;
import es.ujaen.dae.excepciones.*;
import es.ujaen.dae.repositorios.RepositorioActividad;
import es.ujaen.dae.repositorios.RepositorioSocio;
import es.ujaen.dae.repositorios.RepositorioSolicitud;
import es.ujaen.dae.repositorios.RepositorioTemporada;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Validated
public class ServiciosAdmin {
    @Autowired
    private RepositorioSocio repositorioSocio;
    @Autowired
    private RepositorioActividad repositorioActividad;
    @Autowired
    private RepositorioTemporada repositorioTemporada;
    @Autowired
    private RepositorioSolicitud repositorioSolicitud;

    //private HashMap<String,Socio> socios; //Usa el email como clave
    //private HashMap<Integer, Temporada> temporadas; //Usa el año como clave

    public ServiciosAdmin(){
    }


    /**
     *  REGISTRA A UN NUEVO SOCIO
     */
    public Socio crearSocio(@Email @NotBlank String email, @NotBlank String nombre, @NotBlank String apellidos, @NotBlank @Pattern(regexp="^(\\+34|0034|34)?[6789]\\d{8}$") String telefono, @NotBlank String claveAcceso) {
        if(repositorioSocio.existePorEmail(email))
            throw new ClienteRegistrado();
        else{
            Socio s = new Socio(email,nombre,apellidos,telefono,claveAcceso);
            repositorioSocio.guardar(s);
            return s;
        }
    }


    /**
     *  CREA UNA NUEVA ACTIVIDAD
     */
    @Transactional
    public Actividad crearActividad(String titulo, String descripcion, float precio, int plazas, LocalDate fechaCelebracion, LocalDate fechaInicioInscripcion, LocalDate fechaFinInscripcion) {
       Optional<Temporada> t = repositorioTemporada.buscarPorAnio(LocalDate.now().getYear());
       if(t.isPresent()){
           Temporada temporada = t.get();
           Actividad actividad = new Actividad(titulo,descripcion,precio,plazas,fechaCelebracion,fechaInicioInscripcion,fechaFinInscripcion);
           temporada.crearActividad(actividad);
           repositorioActividad.guardar(actividad);
           return actividad;
       }else throw new TemporadaNoExiste();
    }


    /**
     *  CREA NUEVA TEMPORADA Y ASIGNA NO-PAGADO A TODOS LOS SOCIOS
     */
    public Temporada crearTemporada(){
        Optional<Temporada> t = repositorioTemporada.buscarPorAnio(LocalDate.now().getYear());
        if (t.isPresent()) {
            throw new TemporadaYaCreada();
        }
        Temporada temporada = new Temporada(LocalDate.now().getYear());
        repositorioTemporada.guardar(temporada);

        List<Socio> listaSocios = repositorioSocio.getSocios();
        for (Socio s : listaSocios) {
            s.setHaPagado(false);
            repositorioSocio.actualizar(s);
        }
        return temporada;
    }


    /**
     *  ASIGNA LAS PLAZAS DE IDACTIVIDAD AUTOMATICAMENTE
     * @param idActividad id de la actividad en cuestion
     */
    @Transactional
    public void cerrarActividad(int idActividad){
        Optional<Actividad> a = repositorioActividad.buscar(idActividad);

        if (a.isPresent()) {
            Actividad actividad = a.get();
            if (actividad.getFechaFinInscripcion().isBefore(LocalDate.now())) {
                actividad.asignarAutoJusto();
                repositorioActividad.actualizar(actividad);
            } else {
                throw new FechaNoAlcanzada();
            }
        } else throw new ActividadNoExistente();

    }


    /**
     *  DEVUELVE UNA ACTIVIDAD DADO SU ID SI SE ENCUENTRA O NULL
     * @return true o false segun la consulta
     */
    public Actividad buscarActividad(int idActividad){
        Optional<Actividad> a = repositorioActividad.buscar(idActividad);
        if (a.isPresent()) {
            return a.get();
        } else {
            throw new ActividadNoExistente();
        }
    }


    /**
     * Devuelve el objeto socio para logearse
     * @param email email del socio
     * @param clave clave de acceso del socio
     * @return Optional.empty si el login es correcto o Optional.of(Socio) si existe
     */
    public Optional<Socio> login(@Email String email, String clave) {
        return repositorioSocio.buscarPorEmail(email).filter(socio -> socio.getClaveAcceso().equals(clave));
    }


    /**
     * LISTA LAS ACTIVIDADES DISPONIBLES DE LA TEMPORADA ACTUAL
     * @return arraylist con las actividades
     */
    public List<Actividad> listarActividadesDisponibles(){
        return repositorioActividad.buscarActividadesAbiertas();
    }


    /**
     *  REGISTRA QUE UN SOCIO HA PAGADO SU CUOTA
     */
    @Transactional

    public void pagar(@Valid Socio socio){
        socio.setHaPagado(true);
        repositorioSocio.actualizar(socio);
    }


    /**
     * Procesa una solicitud manualmente
     * @param s soliciutd a procesar
     */
    @Transactional
    public void procesarSolicitudManualmente(@Valid Solicitud s, int nPlazas){
        s = repositorioSolicitud.actualizar(s);
        Actividad a = s.getActividad();
        s.getActividad().aceptarSolicitudManual(s, nPlazas);
        repositorioActividad.actualizar(a);
    }


    /**
     *  LISTA LAS SOLICITUDES DE UNA ACTIVIDAD
     * (para que el administrador proceda con la asignacion manual)
     */
    @Transactional
    public List<Solicitud> listarSolicitudesActividad(Actividad a){
       /* if(temporadas.get(LocalDate.now().getYear()).buscarActividad(a.getId()) != null){
            return a.getSolicitudes();
        }else throw new ActividadNoExistente();*/
        a = repositorioActividad.actualizar(a);
        a.getSolicitudes().size();
        return a.getSolicitudes().stream().toList();


    }

    /**
     * Se usa para poder actualiar las fechas de las actividades en los test
     * @param a Actividad que se va a actualizar
     */
    public void actualizarActividad(Actividad a){
        repositorioActividad.actualizar(a);
    }
}
