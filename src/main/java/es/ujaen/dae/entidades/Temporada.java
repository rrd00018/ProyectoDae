package es.ujaen.dae.entidades;

import java.util.Date;
import java.util.HashMap;

public class Temporada {
    private Integer anio;
    private HashMap<Integer,Actividad> actividades; //Contiene el id de la actividad como clave
    private Integer numActividades;

    public Temporada(Integer anio){
        this.anio = anio;
        actividades = new HashMap<>();
        numActividades = 0;
    }
    public void crearActividad(Actividad actividad) {
        numActividades++;
        actividades.put(actividad.getId(),actividad);
    }

    public Actividad buscarActividad(Integer actividad){
        return actividades.get(actividad);
    }

    public Integer getAnio() {return anio;}
    public Integer getIdentificadorActividades() {return numActividades;}
}
