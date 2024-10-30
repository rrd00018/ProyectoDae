package es.ujaen.dae.repositorios;

import es.ujaen.dae.entidades.Socio;
import jakarta.persistence.EntityManager;


import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public class RepositorioSocio {

    @PersistenceContext
    private EntityManager em;

    // Guardar un socio en la base de datos
    @Transactional
    public void guardar(Socio socio) {
        em.persist(socio);
    }

    // Buscar un socio por su ID
    @Transactional(readOnly = true)
    public Optional<Socio> buscarPorId(Long id) {
        return Optional.ofNullable(em.find(Socio.class, id));
    }

    // Buscar un socio por email
    @Transactional(readOnly = true)
    public Optional<Socio> buscarPorEmail(String email) {
        try {
            Socio socio = em.createQuery("SELECT s FROM Socio s WHERE s.email = :email", Socio.class)
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(socio);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    // Verificar si existe un socio por email
    @Transactional(readOnly = true)
    public boolean existePorEmail(String email) {
        Long count = em.createQuery("SELECT COUNT(s) FROM Socio s WHERE s.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    // Actualizar un socio existente
    @Transactional
    public void actualizar(Socio socio) {
        em.merge(socio);
    }

    // Eliminar un socio
    @Transactional
    public void eliminar(Socio socio) {
        em.remove(em.contains(socio) ? socio : em.merge(socio));
    }

    // Buscar todos los socios
    @Transactional(readOnly = true)
    public List<Socio> getSocios() {
        return em.createQuery("SELECT s FROM Socio s", Socio.class).getResultList();
    }

}
