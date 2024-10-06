package es.ujaen.dae.entidades;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Actividad {
    private Integer id;
    private String titulo;
    private String descripcion;
    private Float precio;
    private Integer plazas;
    private Date fechaCelebracion;
    private Date fechaInicioInscripcion;
    private Date fechaFinInscripcion;
    private Integer contadorSolicitudes = 0;
    private ArrayList<Solicitud> solicitudes;

    public Actividad(String titulo, String descripcion, Float precio, Integer plazas, Date fechaCelebracion, Date fechaInicioInscripcion, Date fechaFinInscripcion, Temporada temporada) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.plazas = plazas;
        this.fechaCelebracion = fechaCelebracion;
        this.fechaInicioInscripcion = fechaInicioInscripcion;
        this.fechaFinInscripcion = fechaFinInscripcion;
        this.crearIdActividad(temporada);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    public Integer getPlazas() {
        return plazas;
    }

    public void setPlazas(Integer plazas) {
        this.plazas = plazas;
    }

    public Date getFechaCelebracion() {
        return fechaCelebracion;
    }

    public void setFechaCelebracion(Date fechaCelebracion) {
        this.fechaCelebracion = fechaCelebracion;
    }

    public Date getFechaInicioInscripcion() {
        return fechaInicioInscripcion;
    }

    public void setFechaInicioInscripcion(Date fechaInicioInscripcion) {
        this.fechaInicioInscripcion = fechaInicioInscripcion;
    }

    public Date getFechaFinInscripcion() {
        return fechaFinInscripcion;
    }

    public void setFechaFinInscripcion(Date fechaFinInscripcion) {
        this.fechaFinInscripcion = fechaFinInscripcion;
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
        Integer idSolicitud = this.id * 100 + this.contadorSolicitudes;
        contadorSolicitudes++;
        return idSolicitud;
    }
        //funcion auxiliar del constructor de actividad para generar el id dependiendo de la temporada
    public void crearIdActividad(Temporada temporada) {
        id = temporada.getAnio()*1000 + temporada.getIdentificadorActividades(); // segun la temporada que sea, se genera la actividad teniendo en cuenta la id de la temporada y tras esta el contador de actividades, ej, la temporada 2016 y la actividad 3 seria el id 2016003
    }

    /**
     * @brief Añade una nueva solicitud al conjunto de solicitudes del socio.
     *
     * La solicitud se almacena en un array.
     *
     * @param solicitud La solicitud a ser añadida.
     */
    public void nuevaSolicitud(Solicitud solicitud) {
        solicitudes.add(solicitud);
    }


}
