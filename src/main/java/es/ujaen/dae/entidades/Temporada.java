package es.ujaen.dae.entidades;

import java.util.HashMap;

public class Temporada {
    private Integer anio;
    private HashMap<Integer,Actividad> actividades; //Contiene el id de la actividad como clave
    private Integer identificadorActividades;

    public Temporada(Integer anio){
        this.anio = anio;
        actividades = new HashMap<>();
        identificadorActividades = 0;
    }
    public void crearActividad(Actividad actividad) {
        identificadorActividades++;
        actividad.setId(identificadorActividades);
        actividades.put(identificadorActividades,actividad);
    }

    public Actividad buscarActividad(Integer actividad){
        return actividades.get(actividad);
    }

    public Integer getAnio() {return anio;}
    public Integer getIdentificadorActividades() {return identificadorActividades;}
}
