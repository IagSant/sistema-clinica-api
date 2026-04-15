package psicologa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import psicologa.repository.EventoRepository;
import psicologa.repository.ConsultaRepository;
import psicologa.model.Evento;

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/eventos")
@CrossOrigin
public class EventoController {

    @Autowired
    private EventoRepository repository;

    @Autowired
    private ConsultaRepository consultaRepository;

    @GetMapping
    public List<Evento> listar() {
        return repository.findAll();
    }

    @PostMapping
    public void criar(@RequestBody Map<String, Object> dto) {

        String descricao = dto.get("descricao").toString();
        String data = dto.get("data").toString();
        String hora = dto.get("hora").toString();
        String repeticao = dto.get("repeticao") != null ? dto.get("repeticao").toString() : "";

        List<Integer> diasSemana = List.of();
        if (dto.get("diasSemana") != null) {
            diasSemana = ((List<?>) dto.get("diasSemana"))
                    .stream()
                    .map(d -> Integer.valueOf(d.toString()))
                    .collect(Collectors.toList());
        }

        String grupoId = dto.get("grupoId") != null
                ? dto.get("grupoId").toString()
                : String.valueOf(System.currentTimeMillis());

        LocalDate dataBase = LocalDate.parse(data);
        LocalTime horaBase = LocalTime.parse(hora);

        int total = 8;

        for (int i = 0; i < total; i++) {

            LocalDate dataCalculada = dataBase;

            if ("semanal".equals(repeticao)) {
                dataCalculada = dataBase.plusWeeks(i);
            }

            if ("quinzenal".equals(repeticao)) {
                dataCalculada = dataBase.plusWeeks(i * 2);
            }

            if ("mensal".equals(repeticao)) {
                dataCalculada = dataBase.plusWeeks(i * 4);
            }

            List<Integer> dias = diasSemana.isEmpty()
                    ? List.of(dataCalculada.getDayOfWeek().getValue())
                    : diasSemana;

            for (Integer dia : dias) {

                LocalDate novaData = dataCalculada;

                int diferenca = dia - novaData.getDayOfWeek().getValue();
                if (diferenca < 0) diferenca += 7;

                novaData = novaData.plusDays(diferenca);

                LocalDateTime inicio = LocalDateTime.of(novaData, horaBase);
                LocalDateTime fim = inicio.plusHours(1);

                if (inicio.isBefore(LocalDateTime.now())) continue;

                if (consultaRepository.existsByDataHoraBetween(inicio, fim)) continue;

                if (!repository.findByInicioBetween(inicio, fim).isEmpty()) continue;

                Evento e = new Evento();
                e.setDescricao(descricao);
                e.setInicio(inicio);
                e.setFim(fim);
                e.setGrupoRecorrencia(grupoId);

                repository.save(e);
            }
        }
    }

    @PutMapping("/{id}")
    public Evento atualizar(@PathVariable Long id, @RequestBody Evento novo) {

        Evento evento = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        evento.setDescricao(novo.getDescricao());
        evento.setInicio(novo.getInicio());
        evento.setFim(novo.getFim());
        evento.setGrupoRecorrencia(novo.getGrupoRecorrencia());

        return repository.save(evento);
    }

    @DeleteMapping("/{id}")
    public void deletarUm(@PathVariable Long id) {
        repository.deleteById(id);
    }

    @DeleteMapping("/grupo/{grupo}")
    public void deletarGrupo(@PathVariable String grupo) {
        repository.deleteByGrupoRecorrencia(grupo);
    }
}