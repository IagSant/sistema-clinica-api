package psicologa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ArrayList;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import psicologa.model.Consulta;
import psicologa.model.Paciente;
import psicologa.repository.ConsultaRepository;
import psicologa.repository.PacienteRepository;

@Service
public class ConsultaService {

    @Autowired
    private ConsultaRepository repository;

    @Autowired
    private PacienteRepository pacienteRepository;

    // ✅ AGENDAMENTO NORMAL
    public Consulta agendar(Long pacienteId, Consulta consulta) {

        if (consulta.getDataHora().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível agendar no passado");
        }

        if (repository.existsByDataHora(consulta.getDataHora())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe uma consulta nesse horário!");
        }

        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente não encontrado"));

        consulta.setPaciente(paciente);

        return repository.save(consulta);
    }

    // 🔥 AGENDAMENTO RECORRENTE
    public List<Consulta> agendarRecorrente(
            Long pacienteId,
            LocalDate dataInicio,
            LocalTime hora,
            String repeticao,
            List<Integer> diasSemana
    ) {

        Paciente paciente = pacienteRepository.findById(pacienteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Paciente não encontrado"));

        List<Consulta> consultas = new ArrayList<>();

        LocalDate dataFinal = dataInicio.plusMonths(1); // limite

        for (LocalDate data = dataInicio; !data.isAfter(dataFinal); data = data.plusDays(1)) {

            int diaSemana = data.getDayOfWeek().getValue();

            if (!diasSemana.contains(diaSemana)) continue;

            if (repeticao.equals("quinzenal")) {
                if (ChronoUnit.WEEKS.between(dataInicio, data) % 2 != 0) continue;
            }

            if (repeticao.equals("mensal")) {
                if (data.getDayOfMonth() != dataInicio.getDayOfMonth()) continue;
            }

            LocalDateTime dataHora = LocalDateTime.of(data, hora);

            if (dataHora.isBefore(LocalDateTime.now())) continue;

            if (repository.existsByDataHora(dataHora)) continue;

            Consulta consulta = new Consulta();
            consulta.setPaciente(paciente);
            consulta.setDataHora(dataHora);
            consulta.setStatus("AGENDADA");
            consultas.add(consulta);
        }

        return repository.saveAll(consultas);
    }
}