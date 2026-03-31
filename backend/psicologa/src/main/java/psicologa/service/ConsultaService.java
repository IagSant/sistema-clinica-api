package psicologa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import psicologa.model.Consulta;
import psicologa.repository.ConsultaRepository;
import psicologa.model.Paciente;
import psicologa.repository.PacienteRepository;

@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository repository;

    @Autowired
    private PacienteRepository pacienteRepository;

    public Consulta agendar(Long pacienteId, Consulta consulta) {

        // valida data
        if (consulta.getDataHora().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível agendar no passado");
        }

        // valida duplicidade
        if (repository.existsByDataHora(consulta.getDataHora())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe uma consulta nesse horário!");
        }

        // busca paciente
        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente não encontrado"));

        // relaciona
        consulta.setPaciente(paciente);

        // salva
        return repository.save(consulta);
    }
}