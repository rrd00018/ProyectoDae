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
    private Integer contadorSolicitudes = 0;
    @Getter @Setter
    private Integer id;
    @Getter @Setter
    private String titulo;
    @Getter @Setter
    private String descripcion;
    @Getter @Setter
    private Float precio;
    @Getter @Setter
    private Integer plazas;
    @Getter @Setter
    private LocalDate fechaCelebracion;
    @Getter @Setter
    private LocalDate fechaInicioInscripcion;
    @Getter @Setter
    private LocalDate fechaFinInscripcion;

    public Actividad(String titulo, String descripcion, Float precio, Integer plazas, LocalDate fechaCelebracion, LocalDate fechaInicioInscripcion, LocalDate fechaFinInscripcion, Temporada temporada) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.plazas = plazas;
        this.fechaCelebracion = fechaCelebracion;
        this.fechaInicioInscripcion = fechaInicioInscripcion;
        this.fechaFinInscripcion = fechaFinInscripcion;
        this.plazasAceptadas = new ArrayList<>();
        this.listaEspera = new ArrayList<>();
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
    public Integer generarIdSolicitud() {
        Integer idSolicitud = this.id * 100 + solicitudes.size();
        return idSolicitud;
    }


    /**
     * @brief Añade una nueva solicitud al conjunto de solicitudes del socio.
     *
     * La solicitud se almacena en un array.
     *
     * @param solicitud La solicitud a ser añadida.
     */
    public void addSolicitud(Solicitud solicitud) throws Exception {
        if(plazasAceptadas.size() >= plazas){
            throw new Exception("La actividad esta llena");
        }else{
            solicitudes.add(solicitud);
            if(solicitud.getSocio().getHaPagado()) {
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

    public Integer getNumPlazasAsignadas(){return plazasAceptadas.size();}

    public void moverListaEspera(Integer posiciones){
        for(int i = 0; i < posiciones; i++){
            plazasAceptadas.add(listaEspera.get(i));
        }
    }
}
