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
        if(em.find(Actividad.class,actividad.getId()) != null){
            throw new ActividadYaCreada();
        }else{
            em.persist(actividad);
            em.flush();
        }
    }

    public Actividad actualizar(Actividad actividad){
        return em.merge(actividad);
    }

    public List<Actividad> buscarActividadesAbiertas(){
        return em.createQuery("SELECT a FROM Actividad a WHERE a.fechaFinInscripcion > CURRENT_DATE", Actividad.class).getResultList();
    }
}
