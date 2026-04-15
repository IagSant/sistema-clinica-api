package psicologa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
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
        consulta.setGrupoRecorrencia(null);

        return repository.save(consulta);
    }

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

        if (repeticao == null) repeticao = "nenhum";
        repeticao = repeticao.toLowerCase();

        String grupoId = String.valueOf(System.currentTimeMillis());

        System.out.println("GRUPO GERADO: " + grupoId);

        LocalDate data = dataInicio;

        for (int i = 0; i < 12; i++) {

            LocalDateTime dataHora = LocalDateTime.of(data, hora);

            if (!dataHora.isBefore(LocalDateTime.now()) &&
                    !repository.existsByDataHora(dataHora)) {

                Consulta consulta = new Consulta();
                consulta.setPaciente(paciente);
                consulta.setDataHora(dataHora);
                consulta.setStatus("AGENDADA");

                consulta.setGrupoRecorrencia(grupoId);

                consultas.add(consulta);
            }

            switch (repeticao) {
                case "semanal":
                    data = data.plusWeeks(1);
                    break;

                case "quinzenal":
                    data = data.plusWeeks(2);
                    break;

                case "mensal":
                    data = data.plusMonths(1);
                    break;

                default:
                    i = 999;
                    break;
            }
        }

        return repository.saveAll(consultas);
    }
}