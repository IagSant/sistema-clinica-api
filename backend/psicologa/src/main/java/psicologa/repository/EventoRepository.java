package psicologa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import psicologa.model.Evento;

import java.time.LocalDateTime;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByInicioBetween(LocalDateTime inicio, LocalDateTime fim);

    boolean existsByInicioLessThanAndFimGreaterThan(LocalDateTime fim, LocalDateTime inicio);

    @Transactional
    @Modifying
    void deleteByGrupoId(Long grupoId);
}