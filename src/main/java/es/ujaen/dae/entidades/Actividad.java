package es.ujaen.dae.entidades;

import es.ujaen.dae.excepciones.FechaIncorrecta;
import es.ujaen.dae.excepciones.FechaNoAlcanzada;
import es.ujaen.dae.excepciones.NumeroDePlazasIncorrecto;
import es.ujaen.dae.excepciones.SolicitudIncorrecta;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Actividad {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Getter @Setter @NotNull
    private LocalDate fechaCelebracion;

    @Getter @Setter @NotNull
    private LocalDate fechaInicioInscripcion;

    @Getter @Setter @NotNull
    private LocalDate fechaFinInscripcion;

    private int plazasAsignadas;
    private boolean sociosAsignados;

    @OneToMany(mappedBy = "actividad", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Solicitud> solicitudes;


    public Actividad() {}

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
            plazasAsignadas = 0;
            sociosAsignados = false;
        }
    }

    /**
     * Añade una nueva solicitud a la actividad, si el socio ha pagado y hay hueco se le asigna directamente la plaza
     */
    public void addSolicitud(Solicitud solicitud) {
        solicitudes.add(solicitud);
        solicitud.getSocio().crearSolicitud(solicitud, this);
        if(plazasAsignadas < plazas && solicitud.getSocio().isHaPagado()){ //Si el socio ha pagado se le confirma directamente su plaza
            solicitud.aceptarSolicitud();
            plazasAsignadas++;
        }
    }

    /**
     * Borra una solicitud del conjunto de solicitudes de la actividad
     */
    public void deleteSolicitud(Solicitud solicitud) {
        solicitudes.remove(solicitud);
    }

    /**
     * Simula el procesamiento manual, se intentan asignar las plazas de la solicitud, si no se puede se asignan las maximas posibles.
     * Se aceptan automaticamente todas las solicitudes de los socios que han pagado para evitar que haya fallos en el sistema
     * @param s solicitud a procesar
     */
    public void procesarSolicitudManualmente(Solicitud s, int nPlazas) {
        if(solicitudes.contains(s)){
            if(fechaFinInscripcion.isBefore(LocalDate.now())){
                if(nPlazas > s.getNumAcompaniantes() || nPlazas < 0){ //Compruebo que el numero de plazas no supere lo pedido por la silicitud o que sea negativo
                    throw new NumeroDePlazasIncorrecto();
                }

                if(!sociosAsignados){ //Antes de empezar el procesamiento y la primera vez compruebo que si hay huecos en la lista y socios que han pagado esperando se les asigne plaza
                    asignarSociosQueHanPagado();
                }

                boolean socioPaga = s.getSocio().isHaPagado();

                if(plazasAsignadas + nPlazas <= plazas){ //Si se pueden asignar todos los asigno
                    s.aceptarSolicitud();
                    if(!socioPaga){
                        s.setAcompaniantesAceptados(nPlazas-1); //Si el socio no ha pagado, para calcular las plazas tengo que contar con la suya pero al aceptar acompañantes hay que quitarla
                    }else{
                        s.setAcompaniantesAceptados(nPlazas);
                    }
                    plazasAsignadas += nPlazas; //Aqui no se resta porque si no ha pagado no se ha tenido en cuenta todavia en las plazas asignadas

                }else { //Si no se pueden todos se asignan todos los posibles
                    s.aceptarSolicitud();
                    int aceptados = 0;
                    if(!socioPaga){
                        aceptados = nPlazas - (plazasAsignadas + nPlazas - plazas) - 1; //Se resta el socio a la hora de guardar los acompañanantes
                        s.setAcompaniantesAceptados(aceptados);
                        aceptados++; //Si el socio no habia pagado, hay que tener en cuenta su plaza en plazasAsignadas
                    }else{
                        s.setAcompaniantesAceptados(nPlazas);
                        aceptados += nPlazas;
                    }
                    plazasAsignadas += aceptados;
                }


            }else throw new FechaNoAlcanzada();
        }else throw new SolicitudIncorrecta();
    }


    public void aceptarSolicitudManual(Solicitud s, int nPlazas){
        if(solicitudes.contains(s)){
            if(fechaFinInscripcion.isBefore(LocalDate.now())){
                if(nPlazas > s.getNumAcompaniantes() + 1 || nPlazas <= 0 || plazasAsignadas + nPlazas > plazas)
                    throw new NumeroDePlazasIncorrecto();

                int plazasRequeridas = s.getNumAcompaniantes() - s.getAcompaniantesAceptados();
                if(!s.isAceptada())
                    plazasRequeridas++;


                if(nPlazas > plazasRequeridas)
                    throw new NumeroDePlazasIncorrecto();

                if (!s.isAceptada()) {
                    s.aceptarSolicitud();
                    nPlazas--;
                }

                s.setAcompaniantesAceptados(nPlazas);

            }else throw new FechaNoAlcanzada();
        }else throw new SolicitudIncorrecta();
    }

    /**
     * Asigna las plazas restantes de una actividad de manera automática siguiendo el siguiente esquema. Si por borrados de ultima hora hay socios que han pagado sin plaza se les da.
     * Luego mientras queden plazas y haya solicitudes incompletas, se asignan a los acompañantes de los socios que han pagado primero.
     * Cada dos turnos se permite la asignacion a las solicitudes de socios que no han pagado de una plaza. La primera plaza siempre es la del socio.
     * Al permitir que los socios que no han pagado y sus acompañantes entren en el sistema pero cada dos turnos se premia a los socios que pagan y por consiguiente a sus acompañanates
     */
    public void asignarAutoJusto(){
        if(!sociosAsignados){
            asignarSociosQueHanPagado();
        }
        if(plazasAsignadas < plazas){
            int nVueltas = 0; //Este valor marca si los socios que no han pagado pueden entrar en la asignaciond e plazas o no, si es par acceden al sistem
            int solicitudesCompletas = 0; //Contador para controlar el numero de solicitudes que tienen cumplido su requerimiento de plaza
            while(plazasAsignadas < plazas) {
                if(solicitudesCompletas >=  solicitudes.size()) break; //Si todas las solicitudes han sido completadas se termina el metodo
                for (Solicitud solicitud : solicitudes) {
                    if(plazasAsignadas == plazas) break;
                    if(solicitudesCompletas == solicitudes.size()) break;

                    if (solicitud.getSocio().isHaPagado()) {
                        if(solicitud.getNumAcompaniantes() > solicitud.getAcompaniantesAceptados()) { //Si el socio ha pagao le doy plaza a un acompañante
                            solicitud.setAcompaniantesAceptados(solicitud.getAcompaniantesAceptados() + 1);
                            plazasAsignadas++;
                            if(solicitud.getNumAcompaniantes() == solicitud.getAcompaniantesAceptados()) solicitudesCompletas++; //Si se alcanza el numero de acompañantes doy por cerrada la actividad
                        }
                    }else if(nVueltas % 2 == 1){ //Se elige en las vueltas impares para premiar a los socios que pagan en la primera ronda por si hubiese pocas plazas y se asignasen en un solo ciclo
                        if(solicitud.isAceptada()){
                            if(solicitud.getNumAcompaniantes() > solicitud.getAcompaniantesAceptados()) { //Si la solicitud estuviese aceptada le doy plaza al acompañante
                                solicitud.setAcompaniantesAceptados(solicitud.getAcompaniantesAceptados() + 1);
                                plazasAsignadas++;
                                if(solicitud.getNumAcompaniantes() == solicitud.getAcompaniantesAceptados()) solicitudesCompletas++;
                            }
                        }else{ //Si la solicitud no esta aceptada le doy la plaza al socio q no ha pagado
                            solicitud.aceptarSolicitud();
                            plazasAsignadas++;
                            if(solicitud.getNumAcompaniantes() == 0) solicitudesCompletas++; //Si no tiene peticion de acompañantes doy por cerrada la solicitud
                        }
                    }
                }
                nVueltas++;
            }
        }
    }
    /**
     * Recorre todas las solicitudes y asigna los socios que han pagado.
     * Se debe ejecutar solo una vez al cerrar la actividad por lo que existe la flag sociosAsignados que evita la repeticion de este metodo
     * Esta acción debe ejecutarse independientemente de la forma de gestion de las solicitudes puesto que los socios que han pagado siempre tienen plazas si hay huecos
     */
    public void asignarSociosQueHanPagado(){
        for(Solicitud solicitud : solicitudes){
            if(plazasAsignadas == plazas) break;
            if(solicitud.getSocio().isHaPagado() && !solicitud.isAceptada()){
                solicitud.aceptarSolicitud();
                plazasAsignadas++;
            }
        }
        sociosAsignados = true;
    }

    public  List<Solicitud> getSolicitudes(){return solicitudes;}
}
