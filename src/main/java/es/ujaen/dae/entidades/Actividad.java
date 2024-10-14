package es.ujaen.dae.entidades;

import es.ujaen.dae.excepciones.FechaIncorrecta;
import es.ujaen.dae.excepciones.NumeroDePlazasIncorrecto;
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
    static private int generadorId = 0;

    @Getter
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

    public Actividad(String titulo, String descripcion, float precio, int plazas, LocalDate fechaCelebracion, LocalDate fechaInicioInscripcion, LocalDate fechaFinInscripcion) {
        if(fechaInicioInscripcion.isAfter(fechaFinInscripcion) || fechaInicioInscripcion.isAfter(fechaCelebracion) || fechaFinInscripcion.isAfter(fechaCelebracion)){
            throw new FechaIncorrecta();
        }else {
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
            id = generadorId++;
        }
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
        solicitudes.add(solicitud);
        if(plazasAceptadas.size() >= plazas){
            listaEspera.add(solicitud.getSocio().getEmail());
            for(int i = 0; i < solicitud.getNumAcompaniantes(); i++){
                listaEspera.add(solicitud.getSocio().getEmail());
            }
        }else{
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

    public void addSolicitudCorregida(Solicitud solicitud) {
        solicitudes.add(solicitud);
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

    /**
     * @brief mueve la lista de espera n posiciones y las elimina de la misma
     * @param posiciones
     */
    public void moverListaEspera(int posiciones){
        if(posiciones > plazas - plazasAceptadas.size() ){
            throw new NumeroDePlazasIncorrecto();
        }else {
            for (int i = 0; i < posiciones; i++) {
                plazasAceptadas.add(listaEspera.get(0));
                listaEspera.remove(0);
            }
        }
    }

    public void moverListaEspera2(){
        int plazasAsignadas = 0;
        for(Solicitud solicitud : solicitudes){ //recorre asignandole la plaza a los socios que han pagado
            if(plazasAsignadas == plazas) break;
            if(solicitud.getSocio().isHaPagado()){
                plazasAsignadas++;
                solicitud.aceptarSolicitud();
            }
        }
        if(plazasAsignadas < plazas) {
            for (Solicitud solicitud : solicitudes) { //vuelve a recorrer ahora asignando  plaza a los que quedan dando prioridad a los acompañantes que a los socios que no han pagado porque los acompañantes tienen "pagada su parte po el socio invitador"
                if(plazasAsignadas == plazas) break;
                if (solicitud.isAceptada() && solicitud.getNumAcompaniantes() != solicitud.getAcompaniantesAceptados()) {
                    if (plazasAsignadas + solicitud.getNumAcompaniantes() <= plazas) {
                        solicitud.setAcompaniantesAceptados(solicitud.getNumAcompaniantes());
                        plazasAsignadas += solicitud.getNumAcompaniantes();
                    } else {
                        int aceptados = solicitud.getNumAcompaniantes() - (plazasAsignadas + solicitud.getNumAcompaniantes() - plazas);
                        solicitud.setAcompaniantesAceptados(aceptados);
                        plazasAsignadas += aceptados;
                    }
                }
            }
            if(plazasAsignadas < plazas){ //en caso de que ni con los invitados de los socios que han pagado se llene se pasaria a los socios que no han pagado y a sus acompañantes tratandolos a todos por igual
                for(Solicitud solicitud : solicitudes){
                    if(plazasAsignadas == plazas) break;
                    if(!solicitud.isAceptada()){
                        if (plazasAsignadas + solicitud.getNumAcompaniantes() <= plazas) { //si se pueden asignar directamente se asignan todas las plazas que haya y se acepta la solicitud
                            solicitud.setAcompaniantesAceptados(solicitud.getNumAcompaniantes());
                            plazasAsignadas += solicitud.getNumAcompaniantes();
                            solicitud.aceptarSolicitud();
                        } else { //si solo se pudiesen aceptar unos pocos se acpetan esos y se acepta la solicitud y se le pasa el numero de aceptados
                            int aceptados = solicitud.getNumAcompaniantes() - (plazasAsignadas + solicitud.getNumAcompaniantes() - plazas);
                            solicitud.setAcompaniantesAceptados(aceptados);
                            plazasAsignadas += aceptados;
                            solicitud.aceptarSolicitud();
                        }
                    }
                }
            }
        }
    }
}
