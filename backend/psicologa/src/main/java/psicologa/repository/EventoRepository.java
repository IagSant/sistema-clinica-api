package psicologa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import psicologa.model.Evento;

import java.time.LocalDateTime;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByInicioBetween(LocalDateTime inicio, LocalDateTime fim);

    @Transactional
    @Modifying
    void deleteByGrupoRecorrencia(String grupoRecorrencia);
}