package psicologa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import psicologa.model.Consulta;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    List<Consulta> findByPacienteId(Long pacienteId);

    boolean existsByDataHora(LocalDateTime dataHora);

    List<Consulta> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    boolean existsByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);
}