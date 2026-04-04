package psicologa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import psicologa.model.Consulta;
import psicologa.repository.ConsultaRepository;
import psicologa.model.Paciente;
import psicologa.repository.PacienteRepository;
import psicologa.service.ConsultaService;
import psicologa.dto.DadosAgendamentoDTO;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class ConsultaController {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ConsultaService consultaService; // 👈 FALTAVA ISSO

    // 🔥 SALVAR CONSULTA
    @PostMapping("/salvar-consulta")
    public String salvar(
            @RequestParam Long pacienteId,
            @RequestParam String dataHora
    ) {

        Paciente paciente = pacienteRepository.findById(pacienteId).orElse(null);

        if (paciente == null) {
            return "erro";
        }

        Consulta consulta = new Consulta();

        consulta.setPaciente(paciente);
        consulta.setStatus("PENDENTE");
        consulta.setDataHora(java.time.LocalDateTime.parse(dataHora));

        consultaRepository.save(consulta);

        return "ok";
    }

    // LISTAR TODAS (AGENDA)
    @GetMapping(value = "/consultas", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Consulta> listar() {
        return consultaRepository.findAll();
    }

    // ATUALIZAR
    @PutMapping("/consultas/{id}")
    public Consulta atualizar(@PathVariable Long id, @RequestBody Consulta dados) {

        Consulta consulta = consultaRepository.findById(id).orElseThrow();

        if (dados.getStatus() != null) {
            consulta.setStatus(dados.getStatus());
        }

        if (dados.getObservacao() != null) {
            consulta.setObservacao(dados.getObservacao());
        }

        if (dados.getDataHora() != null) {
            consulta.setDataHora(dados.getDataHora());
        }

        return consultaRepository.save(consulta);
    }

    // DELETAR
    @DeleteMapping("/consultas/{id}")
    public void deletar(@PathVariable Long id) {
        consultaRepository.deleteById(id);
    }

    // 🔥 AGENDAMENTO RECORRENTE
    @PostMapping("/agendar-recorrente")
    public ResponseEntity<?> agendarRecorrente(@RequestBody DadosAgendamentoDTO dados) {
        return ResponseEntity.ok(
                consultaService.agendarRecorrente(
                        dados.getPacienteId(),
                        dados.getData(),
                        dados.getHora(),
                        dados.getRepeticao(),
                        dados.getDiasSemana()
                )
        );
    }
}