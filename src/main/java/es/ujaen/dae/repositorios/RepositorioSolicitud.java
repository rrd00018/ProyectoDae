package es.ujaen.dae.repositorios;

import es.ujaen.dae.entidades.Socio;
import es.ujaen.dae.entidades.Solicitud;
import es.ujaen.dae.entidades.Actividad;
import es.ujaen.dae.excepciones.SolicitudIncorrecta;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public class RepositorioSolicitud {

    @PersistenceContext
    EntityManager em;

    public Optional<Solicitud> buscar(int idSolicitud) {
        return Optional.ofNullable(em.find(Solicitud.class, idSolicitud));
    }

    public void guardar(Solicitud solicitud) {
        em.persist(solicitud);
        em.flush();
    }


    public Solicitud actualizar(Solicitud solicitud) {
        return em.merge(solicitud);
    }


    public List<Solicitud> buscarPorActividad(Actividad actividad) {
        return em.createQuery("SELECT s FROM Solicitud s WHERE s.actividad = :actividad", Solicitud.class)
                .setParameter("actividad", actividad)
                .getResultList();
    }

    public List<Solicitud> buscarSolicitudesAceptadas(Actividad actividad) {
        return em.createQuery("SELECT s FROM Solicitud s WHERE s.actividad = :actividad AND s.aceptada = true", Solicitud.class)
                .setParameter("actividad", actividad)
                .getResultList();
    }
    public void borrarSolicitud(Solicitud solicitud) {
        em.remove(solicitud);
    }
}
