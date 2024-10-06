package es.ujaen.dae.entidades;

import java.util.Date;
import java.util.HashMap;

public class Temporada {
    private Integer anio;
    private HashMap<Integer,Actividad> actividades; //Contiene el id de la actividad como clave
    private Integer identificadorActividades;

    public Temporada(Integer anio){
        this.anio = anio;
        actividades = new HashMap<>();
        identificadorActividades = 1;  //sirve de contador para las actividades, EJ: si una actividad es 2016003 significa que es de la temporada 2016 y es la tercera actividad
    }

    //llama al constructor de actividad que se encarga del proceso completo, tras esto se almacena la actividad creada en el mapa y se aumenta el contador de actividades
    public Actividad crearActividad(String titulo, String descripcion, Float precio, Integer plazas, Date fechaCelebracion, Date fechaInicioInscripcion, Date fechaFinInscripcion) {
        Actividad actividad = new Actividad(titulo,descripcion,precio,plazas,fechaCelebracion,fechaInicioInscripcion,fechaFinInscripcion,this);
        actividades.put(actividad.getId(),actividad);
        identificadorActividades++;
        return actividad;
    }

    public Actividad buscarActividad(Integer actividad){
        return actividades.get(actividad);
    }

    public Integer getAnio() {return anio;}
    public Integer getIdentificadorActividades() {return identificadorActividades;}
}
