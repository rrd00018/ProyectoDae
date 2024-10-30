package es.ujaen.dae.repositorios;

import es.ujaen.dae.entidades.Temporada;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Transactional
@Repository
public class RepositorioTemporada {
@PersistenceContext
EntityManager em;


public Optional<Temporada> buscarTemporada(@Positive int anioTemporada) {
        return Optional.ofNullable(em.find(Temporada.class, anioTemporada));

}

}
