package es.ujaen.dae.repositorios;

import es.ujaen.dae.entidades.Actividad;
import es.ujaen.dae.excepciones.ActividadYaCreada;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public class RepositorioActividad {
    @PersistenceContext
    EntityManager em;

    public RepositorioActividad() {}

    public Optional<Actividad> buscar(int idActividad){
        return Optional.ofNullable(em.find(Actividad.class, idActividad));
    }

    public void guardar(Actividad actividad){
            em.persist(actividad);
    }

    public Actividad actualizar(Actividad actividad){
        Actividad mergedActividad = em.merge(actividad);
        em.flush(); // Fuerza la sincronización de los cambios
        return mergedActividad;
    }

    public List<Actividad> buscarActividadesAbiertas(){
        return em.createQuery("SELECT a FROM Actividad a WHERE a.fechaFinInscripcion > CURRENT_DATE", Actividad.class).getResultList();
    }
}
