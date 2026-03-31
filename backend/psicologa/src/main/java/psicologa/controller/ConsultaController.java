package psicologa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import psicologa.model.Consulta;
import psicologa.repository.ConsultaRepository;
import psicologa.model.Paciente;
import psicologa.repository.PacienteRepository;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api")
public class ConsultaController {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

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

    // ATUALIZAR OBSERVAÇÃO (CORRIGIDO)
    @PutMapping("/consultas/{id}")
    public Consulta atualizar(@PathVariable Long id, @RequestBody Consulta dados) {

        Consulta consulta = consultaRepository.findById(id).orElseThrow();

        // 🔥 ATUALIZA STATUS
        if (dados.getStatus() != null) {
            consulta.setStatus(dados.getStatus());
        }

        // 🔥 ATUALIZA OBSERVAÇÃO
        if (dados.getObservacao() != null) {
            consulta.setObservacao(dados.getObservacao());
        }

        // 🔥 ATUALIZA DATA (caso use)
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
}