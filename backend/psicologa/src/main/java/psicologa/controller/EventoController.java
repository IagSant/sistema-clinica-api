package psicologa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import psicologa.repository.ConsultaRepository;
import psicologa.model.Evento;
import psicologa.repository.EventoRepository;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/eventos")
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
    public Evento criar(@RequestBody Evento evento) {

        // NÃO PERMITIR PASSADO
        if (evento.getInicio().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Não é possível criar evento no passado");
        }

        // CONFLITO COM OUTRO EVENTO (INTERVALO)
        if (repository.existsByInicioLessThanAndFimGreaterThan(evento.getFim(), evento.getInicio())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe um evento nesse intervalo!");
        }

        // CONFLITO COM CONSULTA
        if (consultaRepository.existsByDataHoraBetween(evento.getInicio(), evento.getFim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Já existe uma consulta nesse intervalo!");
        }

        return repository.save(evento);
    }
}