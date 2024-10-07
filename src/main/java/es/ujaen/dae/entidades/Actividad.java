package es.ujaen.dae.entidades;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Actividad {
    private ArrayList<String> plazasAceptadas; //Acepta a los socios que han pagado directamente
    private ArrayList<String> listaEspera; //Almacena los invitados y socios q no han pagado en orden
    private Integer contadorSolicitudes = 0;
    @Getter @Setter
    private ArrayList<Solicitud> solicitudes;
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

    public Actividad(String titulo, String descripcion, Float precio, Integer plazas, Date fechaCelebracion, Date fechaInicioInscripcion, Date fechaFinInscripcion, Temporada temporada) {
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

//---------

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
     * @brief MODIFICA LOS ACOMPAÑANTES EN LA LISTA DE ESPERA
     * @param solicitud La solicitud que se va a modificar
     * @param nuevosInvitados El nuevo número de acompañantes
     */
    public void modificarSolicitud(Solicitud solicitud, Integer nuevosInvitados) throws Exception {
        int invitadosAnteriores = solicitud.getNumAcompaniantes();
        solicitud.setNumAcompaniantes(nuevosInvitados);
        if (nuevosInvitados > invitadosAnteriores) {
            for (int i = 0; i < (nuevosInvitados - invitadosAnteriores); i++) {
                listaEspera.add(solicitud.getSocio().getEmail());
            }
        } else if (nuevosInvitados < invitadosAnteriores) {
            for (int i = 0; i < (invitadosAnteriores - nuevosInvitados); i++) {
                for (int j = listaEspera.size() - 1; j >= 0; j--) {
                    if (listaEspera.get(j).equals(solicitud.getSocio().getEmail())) {
                        listaEspera.remove(j);
                        break;
                    }
                }
            }
        }
    }

    /**
     * @brief Borra una solicitud del conjunto de solicitudes de la actividad
     */
    public void deleteSolicitud(Solicitud solicitud) {
        solicitudes.remove(solicitud);
        for(int i = 0; i < solicitud.getNumAcompaniantes(); i++){
            listaEspera.remove(solicitud.getSocio().getEmail());
        }
    }

    /**
     * @brief RESETEA Y BORRA LAS SOLICITUDES DE USUARIOS Y DE ACTIVIDAD
     * @throws Exception
     */
    public void destroy() throws Exception {
        for (Solicitud solicitud : solicitudes) {
            Socio socio = solicitud.getSocio();
            if (socio != null) {
                socio.cancelarSolicitud(this.getId());
            }
        }
        solicitudes.clear();
    }

}
