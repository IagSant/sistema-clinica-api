package psicologa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import psicologa.model.Evento;

import java.time.LocalDateTime;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByInicioBetween(LocalDateTime inicio, LocalDateTime fim);

    boolean existsByInicioLessThanAndFimGreaterThan(LocalDateTime fim, LocalDateTime inicio);
}