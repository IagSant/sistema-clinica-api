package psicologa.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import jakarta.transaction.Transactional;
import psicologa.model.Consulta;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    List<Consulta> findByPacienteId(Long pacienteId);

    boolean existsByDataHora(LocalDateTime dataHora);

    List<Consulta> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    boolean existsByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    @Modifying
    @Transactional
    @Query("DELETE FROM Consulta c WHERE c.grupoRecorrencia = :grupo")
    void deleteByGrupo(@Param("grupo") String grupo);
}