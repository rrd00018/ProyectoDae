package es.ujaen.dae.entidades;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Entity
public class Temporada {
    @Getter @Setter @Id
    private int anio;
    @Getter @Setter
    private int numActividades;

    @OneToMany
    @JoinColumn(name = "anio") // Crea una columna de clave externa en Actividad
    private Map<Integer, Actividad> actividades = new HashMap<>();


    public Temporada() {
        actividades = new HashMap<>();
        numActividades = 0;
    }

    public Temporada(int anio){
        this.anio = anio;
        actividades = new HashMap<>();
        numActividades = 0;
    }


    /**
     * @brief AÑADE LA ACTIVIDAD AL MAPA
     */
    public void crearActividad(Actividad actividad) {
        numActividades++;
        actividades.put(actividad.getId(),actividad);
    }


    /**
     * @brief BUSCA UNA ACTIVIDAD DADA SU ID
     */
    public Actividad buscarActividad(int actividad){
        return actividades.get(actividad);
    }


    /**
     * @brief LISTA LAS ACTIVIDADES ABIERTAS DISPONIBLES
     */
    public ArrayList<Actividad> listarActividadesEnCurso(){
        ArrayList<Actividad> actividadesEnCurso = new ArrayList<>();
        for(Actividad actividad : actividades.values()){
            if(actividad.getFechaFinInscripcion().isAfter(LocalDate.now())){
                actividadesEnCurso.add(actividad);
            }
        }
        return actividadesEnCurso;
    }
}
