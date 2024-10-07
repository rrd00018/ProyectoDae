package es.ujaen.dae.servicios;

import es.ujaen.dae.entidades.Actividad;
import es.ujaen.dae.entidades.Socio;
import es.ujaen.dae.entidades.Solicitud;
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

//---------

    /**
     * @brief METODO PARA CREAR UN NUEVO SOCIO
     * @param email
     * @param nombre
     * @param apellidos
     * @param telefono
     * @param claveAcceso
     * @return
     * @throws Exception
     */
    public Socio crearSocio(String email, String nombre, String apellidos, Integer telefono, String claveAcceso) throws Exception {
        if(socios.containsKey(email))
            throw new Exception("Cliente ya registrado");
        else{
            Socio s = new Socio(email,nombre,apellidos,telefono,claveAcceso);
            socios.put(email,s);
            return s;
        }
    }

    /**
     * @brief METODO PARA BUSCAR UN SOCIO
     * @param email
     * @return
     */
    public Socio buscarSocio(String email) {
        return socios.get(email);
    }

//---------

    /**
     * @brief METODO QUE CREA UNA ACTIVIDAD A TRAVES DE TEMPORADA
     * @param temporada
     * @param titulo
     * @param descripcion
     * @param precio
     * @param plazas
     * @param fechaCelebracion
     * @param fechaInicioInscripcion
     * @param fechaFinInscripcion
     * @throws Exception
     */
    public void crearActividad(Temporada temporada, String titulo, String descripcion, Float precio, Integer plazas, Date fechaCelebracion, Date fechaInicioInscripcion, Date fechaFinInscripcion) throws Exception {
        if(temporadas.containsValue(temporada)){
            if(fechaCelebracion.before(fechaInicioInscripcion) && fechaCelebracion.before(fechaFinInscripcion)) {
                if(fechaInicioInscripcion.before(fechaFinInscripcion)){
                    temporadas.get(fechaCelebracion.getYear()).crearActividad(titulo,descripcion,precio,plazas,fechaCelebracion,fechaInicioInscripcion,fechaFinInscripcion);
                }else throw new Exception("La fecha de inicio de inscripcion debe ser anterior a la de fin de inscripcion");
            }else throw new Exception("La fecha de inicio de la inscripcion y de fin de la inscripcion deben ser previas a la celebracion de la actividad");
        }else throw new Exception("La temporada no existe");
    }

    /**
     * @brief METODO QUE DEVUELVE UNA ACTIVIDAD SEGUN SU ID
     * @param idActividad
     * @return
     */
    public Actividad buscarActividad( Integer idActividad){
        Integer temporada = idActividad / 1000;
        return temporadas.get(temporada).buscarActividad(idActividad);
    }

    public void cerrarActividad( Integer idActividad) throws Exception {
        Actividad actividad = buscarActividad(idActividad);
        actividad.destroy();
        Integer anioTemporada = actividad.getFechaFinInscripcion().getYear();
        temporadas.get(anioTemporada).cerrarActividad(idActividad);
    }

//---------

    /**
     * @brief METODO PARA CREAR SOLICITUD
     * @param mailSocio
     * @param idActividad
     * @param invitados
     * @return
     * @throws Exception
     */
    public void crearSolicitud(String mailSocio, Integer idActividad, Integer invitados) throws Exception {
        Socio socio = buscarSocio(mailSocio);
        Actividad actividad = buscarActividad(idActividad);
        if (socio == null) {
            throw new Exception("Socio no encontrado");
        }
        if (actividad == null) {
            throw new Exception("Actividad no encontrada");
        }
        if (socio.existeSolicitud(idActividad)) {
            throw new Exception("El socio ya tiene una solicitud para esta actividad");
        }
        Solicitud solicitud = socio.crearSolicitud(actividad, invitados);
        actividad.addSolicitud(solicitud);
    }

    /**
     * @brief METODO PARA MODIFICAR UNA SOLICITUD
     * @param mailSocio Correo del socio
     * @param idActividad ID de la actividad
     * @param nuevosInvitados Número de nuevos invitados
     * @throws Exception Si el socio o la solicitud no son encontrados
     */
    public void modificarSolicitud(String mailSocio, Integer idActividad, Integer nuevosInvitados) throws Exception {
        Socio socio = buscarSocio(mailSocio);
        if (socio == null) {
            throw new Exception("Socio no encontrado");
        }
        try {
            socio.modificarSolicitud(idActividad, nuevosInvitados);
        } catch (Exception e) {
            throw new Exception("Error al modificar la solicitud: " + e.getMessage());
        }
    }

    /**
     * @brief METODO PARA CANCELAR UNA SOLICITUD
     * @param mailSocio
     * @param idActividad
     * @return
     */
    public void cancelarSolicitud(String mailSocio, Integer idActividad) throws Exception {
        Socio socio = buscarSocio(mailSocio);
        if (socio != null) {
            Solicitud solicitud = socio.buscarSolicitud(idActividad); // Obtenemos la solicitud antes de eliminarla
            if (solicitud != null) {
                socio.cancelarSolicitud(idActividad); // Eliminar del socio
                solicitud.getActividad().deleteSolicitud(solicitud); // Eliminar de la actividad
            } else {
                throw new Exception("Solicitud no encontrada para la actividad");
            }
        } else {
            throw new Exception("Socio no encontrado");
        }
    }

//---------

    /**
     * @brief METODO PARA CREAR UNA TEMPORADA
     * @param anio
     * @return
     */
    public Temporada crearTemporada(Integer anio){
        Temporada t = new Temporada(anio);
        temporadas.put(anio,t);
        return t;
    }

    /**
     * @brief METODO PARA BUSCAR UNA TEMPORADA
     * @param idTemporada
     * @return
     */
    public Temporada buscarTemporada(Integer idTemporada) {
        return temporadas.get(idTemporada);
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
