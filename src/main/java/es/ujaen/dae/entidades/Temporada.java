package es.ujaen.dae.entidades;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashMap;

public class Temporada {
    private HashMap<Integer,Actividad> actividades;//Contiene el id de la actividad como clave
    @Getter @Setter
    private Integer anio;
    @Getter @Setter
    private Integer numActividades;

    public Temporada(Integer anio){
        this.anio = anio;
        actividades = new HashMap<>();
        numActividades = 0;
    }
    public void crearActividad(String titulo, String descripcion, Float precio, Integer plazas, Date fechaCelebracion, Date fechaInicioInscripcion, Date fechaFinInscripcion) {
        Actividad actividad = new Actividad(titulo,descripcion,precio,plazas,fechaCelebracion,fechaInicioInscripcion,fechaFinInscripcion,this);
        numActividades++;
        actividades.put(actividad.getId(),actividad);
    }

    public Actividad buscarActividad(Integer actividad){
        return actividades.get(actividad);
    }

    public void cerrarActividad(Integer actividad){
        actividades.remove(actividad);
        numActividades--;
    }
}
