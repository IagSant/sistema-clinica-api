package psicologa.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import psicologa.model.Paciente;

public interface PacienteRepository extends JpaRepository<Paciente, Long> {
}