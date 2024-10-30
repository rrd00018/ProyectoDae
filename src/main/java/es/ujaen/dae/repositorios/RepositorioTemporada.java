package es.ujaen.dae.repositorios;

import es.ujaen.dae.entidades.Temporada;
import jakarta.persistence.EntityManager;

import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class RepositorioTemporada {
        @PersistenceContext
        private EntityManager em;


        public void guardar(Temporada temporada) {em.persist(temporada);}


        public void actualizar(Temporada temporada) {em.merge(temporada);}


        public void eliminar(Temporada temporada) {em.remove(em.contains(temporada) ? temporada : em.merge(temporada));}


        public List<Temporada> getTemporadas() {return em.createQuery("SELECT t FROM Temporada t", Temporada.class).getResultList();}


        public Optional<Temporada> buscarPorAnio(int anio) {
                return Optional.ofNullable(em.find(Temporada.class, anio));
        }


        public boolean existePorAnio(int anio) {
                Long count = em.createQuery("SELECT COUNT(t) FROM Temporada t WHERE t.anio = :anio", Long.class)
                        .setParameter("anio", anio)
                        .getSingleResult();
                return count > 0;
        }
}

