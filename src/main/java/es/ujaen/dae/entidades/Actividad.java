package es.ujaen.dae.entidades;

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
    private HashMap<Integer,Solicitud> solicitudes;

    public Actividad(String titulo, String descripcion, Float precio, Integer plazas, Date fechaCelebracion, Date fechaInicioInscripcion, Date fechaFinInscripcion) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.precio = precio;
        this.plazas = plazas;
        this.fechaCelebracion = fechaCelebracion;
        this.fechaInicioInscripcion = fechaInicioInscripcion;
        this.fechaFinInscripcion = fechaFinInscripcion;
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
    public Integer generarIdSolicitud() {
        // Generar el ID combinando el ID de la actividad y el contador de solicitudes
        Integer idSolicitud = this.id * 100 + this.contadorSolicitudes;
        contadorSolicitudes++;  // Incrementar el contador para la próxima solicitud
        return idSolicitud;
    }

    public void nueva_solicitud(Solicitud solicitud) {
        solicitudes.put(solicitud.getIdSolicitud(), solicitud);
    }
}
