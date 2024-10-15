package es.ujaen.dae.entidades;

import es.ujaen.dae.excepciones.FechaIncorrecta;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;

public class Actividad {
    private ArrayList<Solicitud> solicitudes;
    static private int generadorId = 0;

    @Getter
    private int id;
    @Getter @NotBlank
    private String titulo;
    @Getter
    private String descripcion;
    @Getter @PositiveOrZero
    private float precio;
    @Getter @Positive
    private int plazas;
    @Getter @Setter @NotBlank
    private LocalDate fechaCelebracion;
    @Getter @Setter @NotBlank
    private LocalDate fechaInicioInscripcion;
    @Getter @Setter @NotBlank
    private LocalDate fechaFinInscripcion;

    public Actividad(@NotBlank String titulo, String descripcion, @PositiveOrZero float precio, @Positive int plazas,@NotBlank LocalDate fechaCelebracion,@NotBlank LocalDate fechaInicioInscripcion,@NotBlank LocalDate fechaFinInscripcion) {
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
            this.solicitudes = new ArrayList<>();
            id = generadorId++;
        }
    }

    /**
     *  GENERA UN ID UNICO PARA LA SOLICITUD BASADO EN EL ID DE zzzACTIVIDAD Y EN EL CONTADOR DE SOLICITUDES.
     * El ID de la solicitud se genera multiplicando el ID de la actividad por 100, y sumando un contador
     * de solicitudes que se incrementa con cada nueva solicitud.
     *
     * @return Un número entero que representa el ID único de la solicitud.
     */
    public int generarIdSolicitud() {
        return this.id * 100 + solicitudes.size();
    }

    public void addSolicitud(Solicitud solicitud) {
        solicitudes.add(solicitud);
    }

    /**
     * Borra una solicitud del conjunto de solicitudes de la actividad
     */
    public void deleteSolicitud(Solicitud solicitud) {
        solicitudes.remove(solicitud);
    }

    /**
     * Maneja el fin de la actividad, se aceptan las solicitudes para cumplir las plazas y se informan de cuantas plazas de invitado se han concedido a esa solicitud.
     * En general usa una cola FIFO para aceptar las solicitudes pero matiene el siguiente orden de prioridad: miembros que han pagado -> acompañantes de miembros que han pagado -> miembros que no han pagado y sus acompañanates
     */
    public void moverListaEspera(){
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
                        if (plazasAsignadas + solicitud.getNumAcompaniantes() + 1 <= plazas) { //si se pueden asignar directamente se asignan todas las plazas que haya y se acepta la solicitud. El +1 representa al socio en cuestion
                            solicitud.setAcompaniantesAceptados(solicitud.getNumAcompaniantes());
                            plazasAsignadas += solicitud.getNumAcompaniantes() +1;
                            solicitud.aceptarSolicitud();
                        } else { //si solo se pudiesen aceptar unos pocos se acpetan esos y se acepta la solicitud y se le pasa el numero de aceptados. El -1 representa al socio en cuestion
                            int aceptados = solicitud.getNumAcompaniantes() - (plazasAsignadas + solicitud.getNumAcompaniantes() - plazas) -1;
                            solicitud.setAcompaniantesAceptados(aceptados);
                            plazasAsignadas += aceptados + 1; //Aqui se mete el socio para tenerlo en cuenta en el total de plazas
                            solicitud.aceptarSolicitud();
                        }
                    }
                }
            }
        }
    }
}
