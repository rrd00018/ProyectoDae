package es.ujaen.dae.rest.dto;

import es.ujaen.dae.entidades.Actividad;
import es.ujaen.dae.entidades.Socio;
import es.ujaen.dae.entidades.Solicitud;
import es.ujaen.dae.entidades.Temporada;
import es.ujaen.dae.excepciones.ActividadNoExistente;
import es.ujaen.dae.excepciones.UsuarioNoRegistrado;
import es.ujaen.dae.repositorios.RepositorioActividad;
import es.ujaen.dae.repositorios.RepositorioSocio;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import es.ujaen.dae.excepciones.ActividadNoExistente;

@Service
public class Mapeador {
    @Autowired
    RepositorioActividad repositorioActividad;

    @Autowired
    RepositorioSocio repositorioSocio;

    @Autowired
    PasswordEncoder codificador;

    public Mapeador(RepositorioActividad repositorioActividad) {
        this.repositorioActividad = repositorioActividad;
    }

    public DSocio dto(@NotNull Socio socio){
        return new DSocio(socio.getIdSocio(),socio.getEmail(),socio.getNombre(),socio.getApellidos(),socio.getTelefono(),"",socio.isHaPagado());
    }

    public Socio entidad(@NotNull DSocio socio){
        return new Socio(socio.idSocio(),socio.email(),socio.nombre(),socio.apellidos(),socio.telefono(),socio.claveAcceso(),socio.haPagado());
    }

    public Socio nuevaEntidad(@NotNull DSocio socio){
        return new Socio(socio.idSocio(),socio.email(),socio.nombre(),socio.apellidos(),socio.telefono(), codificador.encode(socio.claveAcceso()), socio.haPagado());
    }

    public DTemporada dto(@NotNull Temporada temporada){
        return new DTemporada(temporada.getAnio());
    }

    public Temporada entidad(@NotNull DTemporada temporada){
        return new Temporada(temporada.anio());
    }

    public DActividad dto(@NotNull Actividad actividad){
        return new DActividad(actividad.getId(), actividad.getTitulo(), actividad.getDescripcion(), actividad.getPrecio(), actividad.getPlazas(), actividad.getFechaCelebracion(), actividad.getFechaInicioInscripcion(), actividad.getFechaFinInscripcion(), actividad.getPlazasAsignadas());
    }

    public Actividad entidad(@NotNull DActividad actividad){
        return new Actividad(actividad.id(),actividad.titulo(),actividad.descripcion(),actividad.precio(),actividad.plazas(),actividad.fechaCelebracion(),actividad.fechaInicioInscripcion(),actividad.fechaFinInscripcion(),actividad.plazasAsignadas());
    }

    public DSolicitud dto(@NotNull Solicitud solicitud){
        return new DSolicitud(solicitud.getIdSolicitud(), solicitud.getNumAcompaniantes(), solicitud.isAceptada(), solicitud.getAcompaniantesAceptados(), solicitud.getSocio().getIdSocio(), solicitud.getActividad().getId());
    }

    public Solicitud entidad(@NotNull DSolicitud solicitud){
        Actividad actividad = repositorioActividad.buscar(solicitud.idActividad()).orElseThrow(ActividadNoExistente::new);
        Socio socio = repositorioSocio.buscarPorId(solicitud.idSocio()).orElseThrow(UsuarioNoRegistrado::new);
        return new Solicitud(solicitud.idSolicitud(), solicitud.numAcompaniantes(), solicitud.aceptada(), solicitud.acompaniantesAceptados(), socio, actividad);
    }
}
