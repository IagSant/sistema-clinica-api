package psicologa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import psicologa.model.Consulta;
import psicologa.model.Evento;
import psicologa.model.Paciente;

import psicologa.repository.ConsultaRepository;
import psicologa.repository.EventoRepository;
import psicologa.repository.PacienteRepository;
import java.time.temporal.TemporalAdjusters;
import jakarta.transaction.Transactional;

import java.time.*;
import java.util.*;
import java.util.UUID;

@RestController
@RequestMapping("/agenda")
@CrossOrigin
public class AgendaController {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private EventoRepository eventoRepository;


    // AGENDA POR DIA
    @GetMapping("/dia")
    public List<Map<String, Object>> agendaDia(@RequestParam String data) {

        LocalDate dia = LocalDate.parse(data);

        LocalDateTime inicio = dia.atStartOfDay();
        LocalDateTime fim = dia.atTime(23, 59, 59);

        List<Consulta> consultas = consultaRepository.findByDataHoraBetween(inicio, fim);
        List<Evento> eventos = eventoRepository.findByInicioBetween(inicio, fim);

        List<Map<String, Object>> lista = new ArrayList<>();

        for (Consulta c : consultas) {
            Map<String, Object> item = new HashMap<>();
            item.put("tipo", "consulta");
            item.put("horario", c.getDataHora());
            item.put("descricao", "Consulta com " + c.getPaciente().getNome());
            lista.add(item);
        }

        for (Evento e : eventos) {
            Map<String, Object> item = new HashMap<>();
            item.put("tipo", "evento");
            item.put("horario", e.getInicio());
            item.put("descricao", e.getDescricao());
            lista.add(item);
        }

        lista.sort(Comparator.comparing(a -> (LocalDateTime) a.get("horario")));

        return lista;
    }

    // AGENDA SEMANAL
    @GetMapping("/semana")
    public Map<String, List<Map<String, Object>>> agendaSemana(@RequestParam String data) {

        LocalDate dia = LocalDate.parse(data);
        LocalDate inicioSemana = dia.with(DayOfWeek.SUNDAY);

        Map<String, List<Map<String, Object>>> agenda = new LinkedHashMap<>();

        for (int i = 0; i < 7; i++) {

            LocalDate diaAtual = inicioSemana.plusDays(i);

            LocalDateTime inicio = diaAtual.atStartOfDay();
            LocalDateTime fim = diaAtual.atTime(23, 59, 59);

            List<Consulta> consultas = consultaRepository.findByDataHoraBetween(inicio, fim);
            List<Evento> eventos = eventoRepository.findByInicioBetween(inicio, fim);

            List<Map<String, Object>> listaDia = new ArrayList<>();

            for (Consulta c : consultas) {
                Map<String, Object> item = new HashMap<>();
                item.put("tipo", "consulta");
                item.put("horario", c.getDataHora());
                item.put("descricao", "Consulta com " + c.getPaciente().getNome());
                listaDia.add(item);
            }

            for (Evento e : eventos) {
                Map<String, Object> item = new HashMap<>();
                item.put("tipo", "evento");
                item.put("horario", e.getInicio());
                item.put("descricao", e.getDescricao());
                listaDia.add(item);
            }

            listaDia.sort(Comparator.comparing(a -> (LocalDateTime) a.get("horario")));

            agenda.put(diaAtual.toString(), listaDia);
        }

        return agenda;
    }

    // HORÁRIOS DISPONÍVEIS
    @GetMapping("/disponivel")
    public List<String> horariosDisponiveis(@RequestParam String data) {

        LocalDate dia = LocalDate.parse(data);

        LocalDateTime inicioDia = dia.atTime(8, 0);
        LocalDateTime fimDia = dia.atTime(18, 0);

        List<String> disponiveis = new ArrayList<>();

        List<Consulta> consultas = consultaRepository.findByDataHoraBetween(
                dia.atStartOfDay(), dia.atTime(23, 59, 59));

        List<Evento> eventos = eventoRepository.findByInicioBetween(
                dia.atStartOfDay(), dia.atTime(23, 59, 59));

        for (LocalDateTime horario = inicioDia;
             horario.isBefore(fimDia);
             horario = horario.plusHours(1)) {

            LocalDateTime fimConsulta = horario.plusHours(1);
            boolean ocupado = false;

            for (Consulta c : consultas) {
                LocalDateTime inicioConsulta = c.getDataHora();
                LocalDateTime fimC = inicioConsulta.plusHours(1);

                if (inicioConsulta.isBefore(fimConsulta) && fimC.isAfter(horario)) {
                    ocupado = true;
                    break;
                }
            }

            if (!ocupado) {
                for (Evento e : eventos) {
                    if (e.getInicio().isBefore(fimConsulta) && e.getFim().isAfter(horario)) {
                        ocupado = true;
                        break;
                    }
                }
            }

            if (!ocupado) {
                disponiveis.add(horario.toLocalTime().toString());
            }
        }

        return disponiveis;
    }


    // AGENDAR CONSULTA
    @PostMapping("/agendar")
    public List<Consulta> agendar(@RequestBody Map<String, Object> dados) {

        Long pacienteId = Long.parseLong(dados.get("pacienteId").toString());
        String data = dados.get("data").toString();
        String hora = dados.get("hora").toString();

        List<Integer> diasSemana = (List<Integer>) dados.get("diasSemana");

        Paciente paciente = pacienteRepository.findById(pacienteId).orElseThrow();

        List<Consulta> consultasCriadas = new ArrayList<>();

        LocalDate dataBase = LocalDate.parse(data);

        // gera grupo da recorrência
        String grupo = UUID.randomUUID().toString();

        // se não vier dias, usa o próprio dia
        if (diasSemana == null || diasSemana.isEmpty()) {
            diasSemana = List.of(dataBase.getDayOfWeek().getValue());
        }

        for (Integer diaSemana : diasSemana) {

            DayOfWeek dia = DayOfWeek.of(diaSemana);

            LocalDate primeiraData = dataBase.with(
                    TemporalAdjusters.nextOrSame(dia)
            );

            for (int semana = 0; semana < 24; semana++) {

                LocalDate dataConsulta = primeiraData.plusWeeks(semana);

                LocalDateTime dataHoraFinal =
                        LocalDateTime.parse(dataConsulta + "T" + hora);

                // VALIDAÇÃO CORRETA (sem bug)
                boolean ocupado = consultaRepository
                        .existsByDataHora(dataHoraFinal);

                if (!ocupado) {

                    Consulta consulta = new Consulta();
                    consulta.setDataHora(dataHoraFinal);
                    consulta.setPaciente(paciente);

                    // ESSENCIAL (isso resolve seu problema)
                    consulta.setGrupoRecorrencia(grupo);

                    consultasCriadas.add(consultaRepository.save(consulta));
                }
            }
        }

        return consultasCriadas;
    }

    @DeleteMapping("/consulta/{id}")
    @Transactional
    public void deletarConsulta(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean todos
    ) {

        Consulta consulta = consultaRepository.findById(id).orElseThrow();

        System.out.println("ID: " + id);
        System.out.println("TODOS: " + todos);
        System.out.println("GRUPO: " + consulta.getGrupoRecorrencia());

        if (todos && consulta.getGrupoRecorrencia() != null) {

            System.out.println("DELETANDO GRUPO...");

            consultaRepository.deleteByGrupo(
                    consulta.getGrupoRecorrencia()
            );

        } else {
            System.out.println("DELETANDO UM...");
            consultaRepository.deleteById(id);
        }
    }
}