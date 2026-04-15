package psicologa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import psicologa.model.Consulta;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    List<Consulta> findByPacienteId(Long pacienteId);

    List<Consulta> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    boolean existsByDataHora(LocalDateTime dataHora);

    boolean existsByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    @Transactional
    @Modifying
    void deleteByGrupoRecorrencia(String grupoRecorrencia);
}