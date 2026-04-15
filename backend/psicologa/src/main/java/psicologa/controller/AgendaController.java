package psicologa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import psicologa.model.Consulta;
import psicologa.model.Evento;
import psicologa.repository.ConsultaRepository;
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

    @GetMapping("/semana")
    public Map<String, List<Map<String, Object>>> agendaSemana(@RequestParam String data) {

        LocalDate dia = LocalDate.parse(data);
        LocalDate inicioSemana = dia.with(DayOfWeek.SUNDAY);

        Map<String, List<Map<String, Object>>> agenda = new LinkedHashMap<>();

        LocalDateTime inicio = inicioSemana.atStartOfDay();
        LocalDateTime fim = inicioSemana.plusDays(6).atTime(23, 59, 59);

        List<Consulta> consultas = consultaRepository.findByDataHoraBetween(inicio, fim);
        List<Evento> eventos = eventoRepository.findByInicioBetween(inicio, fim);

        for (int i = 0; i < 7; i++) {

            LocalDate diaAtual = inicioSemana.plusDays(i);

            LocalDateTime inicioDia = diaAtual.atStartOfDay();
            LocalDateTime fimDia = diaAtual.atTime(23, 59, 59);

            List<Map<String, Object>> listaDia = new ArrayList<>();

            for (Consulta c : consultas) {

                if (c.getDataHora().isBefore(inicioDia) || c.getDataHora().isAfter(fimDia)) {
                    continue;
                }

                Map<String, Object> item = new HashMap<>();
                item.put("tipo", "consulta");
                item.put("horario", c.getDataHora());
                item.put("descricao", "Consulta com " + c.getPaciente().getNome());
                listaDia.add(item);
            }

            for (Evento e : eventos) {

                if (e.getFim().isBefore(inicioDia) || e.getInicio().isAfter(fimDia)) {
                    continue;
                }

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

    @DeleteMapping("/consulta/{id}")
    public void deletarConsulta(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "false") boolean todos
    ) {

        Consulta consulta = consultaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Consulta não encontrada"));

        if (todos) {

            String grupo = consulta.getGrupoRecorrencia();

            if (grupo != null && !grupo.isEmpty()) {
                consultaRepository.deleteByGrupoRecorrencia(grupo);
            } else {
                consultaRepository.deleteById(id);
            }

        } else {
            consultaRepository.deleteById(id);
        }
    }
}