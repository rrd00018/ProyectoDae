package es.ujaen.dae.entidades;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Actividad {
    private ArrayList<Solicitud> solicitudes;
    private ArrayList<String> plazasAceptadas; //Acepta a los socios que han pagado directamente
    private ArrayList<String> listaEspera; //Almacena los invitados y socios q no han pagado en orden
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
    private Date fechaCelebracion;
    @Getter @Setter
    private Date fechaInicioInscripcion;
    @Getter @Setter
    private Date fechaFinInscripcion;
    private Integer contadorSolicitudes = 0;

    public Actividad(String titulo, String descripcion, Float precio, Integer plazas, Date fechaCelebracion, Date fechaInicioInscripcion, Date fechaFinInscripcion, Temporada temporada) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.plazas = plazas;
        this.fechaCelebracion = fechaCelebracion;
        this.fechaInicioInscripcion = fechaInicioInscripcion;
        this.fechaFinInscripcion = fechaFinInscripcion;
        this.crearIdActividad(temporada);
        this.plazasAceptadas = new ArrayList<>();
        this.listaEspera = new ArrayList<>();
    }

    /**
     * @brief Genera un ID único para las solicitudes basado en el ID de la actividad y un contador secuencial.
     *
     * El ID de la solicitud se genera multiplicando el ID de la actividad por 100, y sumando un contador
     * de solicitudes que se incrementa con cada nueva solicitud. De esta manera, las solicitudes para
     * cada actividad tendrán un ID único del tipo "actividadID + número de solicitud".
     *
     * @return Un número entero que representa el ID único de la solicitud.
     */
    public Integer generarIdSolicitud() {
        // Generar el ID combinando el ID de la actividad y el contador de solicitudes
        Integer idSolicitud = this.id * 100 + solicitudes.size();
        return idSolicitud;
    }
        //funcion auxiliar del constructor de actividad para generar el id dependiendo de la temporada
    public void crearIdActividad(Temporada temporada) {
        id = temporada.getAnio()*1000 + temporada.getNumActividades(); // segun la temporada que sea, se genera la actividad teniendo en cuenta la id de la temporada y tras esta el contador de actividades, ej, la temporada 2016 y la actividad 3 seria el id 2016003
    }

    /**
     * @brief Añade una nueva solicitud al conjunto de solicitudes del socio.
     *
     * La solicitud se almacena en un array.
     *
     * @param solicitud La solicitud a ser añadida.
     */
    public void nuevaSolicitud(Solicitud solicitud) throws Exception {
        if(plazasAceptadas.size() >= plazas){
            throw new Exception("La actividad esta llena");
        }else{
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
     */
    public void deleteSolicitud(Solicitud solicitud) {
        solicitudes.remove(solicitud);
    }

}
