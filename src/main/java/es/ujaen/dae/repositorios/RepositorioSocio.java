package es.ujaen.dae.repositorios;

import es.ujaen.dae.entidades.Socio;
import jakarta.persistence.EntityManager;

import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class RepositorioSocio {

    @PersistenceContext
    private EntityManager em;


    public void guardar(Socio socio) {
        em.persist(socio);
    }


    public Socio actualizar(Socio socio) {
        Socio mergedSocio = em.merge(socio);
        em.flush();
        return mergedSocio;
    }


    public void eliminar(Socio socio) {
        em.remove(em.contains(socio) ? socio : em.merge(socio));
    }


    public List<Socio> getSocios() {
        return em.createQuery("SELECT s FROM Socio s", Socio.class).getResultList();
    }


    public Optional<Socio> buscarPorId(int id) {
        return Optional.ofNullable(em.find(Socio.class, id));
    }


    public Optional<Socio> buscarPorEmail(String email) {
        return em.createQuery("SELECT s FROM Socio s WHERE s.email = :email", Socio.class)
                .setParameter("email", email)
                .getResultStream()
                .findAny();
    }
}
