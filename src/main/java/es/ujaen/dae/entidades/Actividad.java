package es.ujaen.dae.entidades;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Actividad {
    private ArrayList<Solicitud> solicitudes;
    private ArrayList<String> plazasAceptadas; //Acepta a los socios que han pagado directamente
    private ArrayList<String> listaEspera; //Almacena los invitados y socios q no han pagado en orden
    @Getter @Setter
    private int id;
    @Getter @Setter
    private String titulo;
    @Getter @Setter
    private String descripcion;
    @Getter @Setter
    private float precio;
    @Getter @Setter
    private int plazas;
    @Getter @Setter
    private LocalDate fechaCelebracion;
    @Getter @Setter
    private LocalDate fechaInicioInscripcion;
    @Getter @Setter
    private LocalDate fechaFinInscripcion;

    public Actividad(String titulo, String descripcion, float precio, int plazas, LocalDate fechaCelebracion, LocalDate fechaInicioInscripcion, LocalDate fechaFinInscripcion, Temporada temporada) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.plazas = plazas;
        this.fechaCelebracion = fechaCelebracion;
        this.fechaInicioInscripcion = fechaInicioInscripcion;
        this.fechaFinInscripcion = fechaFinInscripcion;
        this.plazasAceptadas = new ArrayList<>();
        this.listaEspera = new ArrayList<>();
        this.solicitudes = new ArrayList<>();
        id = temporada.getAnio()*1000 + temporada.getNumActividades();
    }

    /**
     * @brief GENERA UN ID UNICO PARA LA SOLICITUD BASADO EN EL ID DE zzzACTIVIDAD Y EN EL CONTADOR DE SOLICITUDES.
     *
     * El ID de la solicitud se genera multiplicando el ID de la actividad por 100, y sumando un contador
     * de solicitudes que se incrementa con cada nueva solicitud.
     *
     * @return Un número entero que representa el ID único de la solicitud.
     */
    public int generarIdSolicitud() {
        return this.id * 100 + solicitudes.size();
    }


    /**
     * @brief Añade una nueva solicitud al conjunto de solicitudes del socio.
     *
     * La solicitud se almacena en un array.
     *
     * @param solicitud La solicitud a ser añadida.
     */
    public void addSolicitud(Solicitud solicitud) {
        if(plazasAceptadas.size() >= plazas){
            listaEspera.add(solicitud.getSocio().getEmail());
            for(int i = 0; i < solicitud.getNumAcompaniantes(); i++){
                listaEspera.add(solicitud.getSocio().getEmail());
            }
        }else{
            solicitudes.add(solicitud);
            if(solicitud.getSocio().isHaPagado()) {
                plazasAceptadas.add(solicitud.getSocio().getEmail());
            }else{
                listaEspera.add(solicitud.getSocio().getEmail());
            }
            for(int i = 0; i < solicitud.getNumAcompaniantes(); i++){
                listaEspera.add(solicitud.getSocio().getEmail());
            }
        }
    }


    /**
     * @brief Borra una solicitud del conjunto de solicitudes de la actividad
     * Cuando se borra la solicitud, se borran todas las instacias en las listas de espera
     */
    public void deleteSolicitud(Solicitud solicitud) {
        plazasAceptadas.remove(solicitud.getSocio().getEmail());
        if(solicitud.getNumAcompaniantes() > 0){
            for(int i = 0; i < solicitud.getNumAcompaniantes(); i++){
                listaEspera.remove(solicitud.getSocio().getEmail());
            }
        }
        solicitudes.remove(solicitud);
    }

    public int getNumPlazasAsignadas(){return plazasAceptadas.size();}

    public void moverListaEspera(int posiciones){
        for(int i = 0; i < posiciones; i++){
            plazasAceptadas.add(listaEspera.get(i));
        }
    }
}
