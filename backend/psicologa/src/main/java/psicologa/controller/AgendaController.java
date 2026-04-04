package psicologa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import psicologa.model.Consulta;
import psicologa.repository.ConsultaRepository;
import psicologa.model.Evento;
import psicologa.repository.EventoRepository;

import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/agenda")
@CrossOrigin
public class AgendaController {

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

        // CONSULTAS
        for (Consulta c : consultas) {
            Map<String, Object> item = new HashMap<>();
            item.put("tipo", "consulta");
            item.put("horario", c.getDataHora());
            item.put("descricao", "Consulta com " + c.getPaciente().getNome());
            lista.add(item);
        }

        // EVENTOS
        for (Evento e : eventos) {
            Map<String, Object> item = new HashMap<>();
            item.put("tipo", "evento");
            item.put("horario", e.getInicio());
            item.put("descricao", e.getDescricao());
            lista.add(item);
        }

        // ORDENAR POR HORÁRIO
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

            // CONSULTAS
            for (Consulta c : consultas) {
                Map<String, Object> item = new HashMap<>();
                item.put("tipo", "consulta");
                item.put("horario", c.getDataHora());
                item.put("descricao", "Consulta com " + c.getPaciente().getNome());
                listaDia.add(item);
            }

            // EVENTOS
            for (Evento e : eventos) {
                Map<String, Object> item = new HashMap<>();
                item.put("tipo", "evento");
                item.put("horario", e.getInicio());
                item.put("descricao", e.getDescricao());
                listaDia.add(item);
            }

            // ORDENAR
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

            // CONSULTAS
            for (Consulta c : consultas) {
                LocalDateTime inicioConsulta = c.getDataHora();
                LocalDateTime fimC = inicioConsulta.plusHours(1);

                if (inicioConsulta.isBefore(fimConsulta) && fimC.isAfter(horario)) {
                    ocupado = true;
                    break;
                }
            }

            // EVENTOS
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
}