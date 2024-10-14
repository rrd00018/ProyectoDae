package es.ujaen.dae.entidades;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class Temporada {
    private HashMap<Integer,Actividad> actividades;//Contiene el id de la actividad como clave
    @Getter @Setter
    private int anio;
    @Getter @Setter
    private int numActividades;

    public Temporada(int anio){
        this.anio = anio;
        actividades = new HashMap<>();
        numActividades = 0;
    }
    public void crearActividad(Actividad actividad) {
        numActividades++;
        actividades.put(actividad.getId(),actividad);
    }

    public Actividad buscarActividad(int actividad){
        return actividades.get(actividad);
    }

    public ArrayList<Actividad> listarActividadesEnCurso(){
        ArrayList<Actividad> actividadesEnCurso = new ArrayList<>();
        for(Actividad actividad : actividades.values()){
            if(actividad.getFechaFinInscripcion().isBefore(LocalDate.now())){
                actividadesEnCurso.add(actividad);
            }
        }
        return actividadesEnCurso;
    }
}
