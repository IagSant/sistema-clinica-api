package psicologa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import psicologa.model.Consulta;

import java.time.LocalDateTime;
import java.util.List;

public interface ConsultaRepository extends JpaRepository<Consulta, Long> {

    List<Consulta> findByPacienteId(Long pacienteId);

    boolean existsByDataHora(LocalDateTime dataHora);

    List<Consulta> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    boolean existsByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    // DELETE EM MASSA (VERSÃO FINAL CORRETA)
    @Modifying
    @Transactional
    @Query("DELETE FROM Consulta c WHERE c.grupoRecorrencia = :grupo")
    int deleteByGrupo(@Param("grupo") String grupo);
}