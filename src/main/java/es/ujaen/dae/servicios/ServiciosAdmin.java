package es.ujaen.dae.servicios;

import es.ujaen.dae.entidades.Actividad;
import es.ujaen.dae.entidades.Socio;
import es.ujaen.dae.entidades.Temporada;
import es.ujaen.dae.excepciones.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

@Service
@Validated
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

    public Socio crearSocio(@Email @Valid String email, String nombre, String apellidos, int telefono, String claveAcceso) {
        if(socios.containsKey(email))
            throw new ClienteRegistrado();
        else{
            Socio s = new Socio(email,nombre,apellidos,telefono,claveAcceso);
            socios.put(email,s);
            return s;
        }
    }

    public Actividad crearActividad(String titulo, String descripcion, float precio, int plazas, LocalDate fechaCelebracion, LocalDate fechaInicioInscripcion, LocalDate fechaFinInscripcion) {
        if(temporadas.containsKey(LocalDate.now().getYear())){
            Actividad actividad = new Actividad(titulo,descripcion,precio,plazas,fechaCelebracion,fechaInicioInscripcion,fechaFinInscripcion);
            temporadas.get(fechaCelebracion.getYear()).crearActividad(actividad);
            return actividad;
        }else throw new TemporadaNoExiste();
    }

    /**
     * @brief Crea una nueva temporada e inicializa el campo pagado de todos los socios a false
     */
    public Temporada crearTemporada(){
        if(!temporadas.containsKey(LocalDate.now().getYear())){
            Temporada t = new Temporada(LocalDate.now().getYear());
            temporadas.put(LocalDate.now().getYear(),t);
            return temporadas.get(LocalDate.now().getYear());
        }else throw new TemporadaYaCreada();
    }



    public void cerrarActividad(int idActividad){
        Actividad a = temporadas.get(LocalDate.now().getYear()).buscarActividad(idActividad);
        if(a.getFechaFinInscripcion().isBefore(LocalDate.now())) {
            a.moverListaEspera();
        }
    }

    /**
     * @brief comprueba si una actividad existe en su temporada
     * @param idActividad
     * @return true o false segun la consulta
     */
    public Actividad buscarActividad(int idActividad){
        return temporadas.get(LocalDate.now().getYear()).buscarActividad(idActividad);
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

    /**
     * @brief Lista todas las actividades disponibles para apuntarse de la temporada actual
     * @return arraylist con las actividades
     */
    public ArrayList<Actividad> listarActividadesDisponibles(){
        return temporadas.get(LocalDate.now().getYear()).listarActividadesEnCurso();
    }

    public void pagar(Socio socio){
        socio.setHaPagado(true);
    }
}
