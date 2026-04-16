package psicologa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import psicologa.model.Consulta;
import psicologa.model.Paciente;
import psicologa.repository.ConsultaRepository;
import psicologa.repository.PacienteRepository;

import org.springframework.http.ResponseEntity;

import java.time.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/consultas")
@CrossOrigin
public class ConsultaController {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @PostMapping
    public ResponseEntity<?> criarConsulta(@RequestBody Map<String, Object> dto) {

        Long pacienteId = Long.valueOf(dto.get("pacienteId").toString());
        String data = dto.get("data").toString();
        String hora = dto.get("hora").toString();
        String repeticao = dto.get("repeticao").toString();

        LocalDate dataBase = LocalDate.parse(data);
        LocalTime horaBase = LocalTime.parse(hora);

        String grupoId = dto.get("grupoId") != null
                ? dto.get("grupoId").toString()
                : String.valueOf(System.currentTimeMillis());

        int totalOcorrencias = 8;

        Paciente paciente = pacienteRepository.findById(pacienteId).orElse(null);

        if (paciente == null) {
            return ResponseEntity.badRequest().body("Paciente não encontrado");
        }

        if ("semanal".equals(repeticao)) {

            for (int i = 0; i < totalOcorrencias; i++) {
                Consulta c = new Consulta();
                c.setPaciente(paciente);
                c.setDataHora(LocalDateTime.of(dataBase.plusWeeks(i), horaBase));
                c.setGrupoRecorrencia(grupoId);
                c.setStatus("PENDENTE");
                consultaRepository.save(c);
            }

        } else if ("quinzenal".equals(repeticao)) {

            for (int i = 0; i < totalOcorrencias; i++) {
                Consulta c = new Consulta();
                c.setPaciente(paciente);
                c.setDataHora(LocalDateTime.of(dataBase.plusWeeks(i * 2), horaBase));
                c.setGrupoRecorrencia(grupoId);
                c.setStatus("PENDENTE");
                consultaRepository.save(c);
            }

        } else if ("mensal".equals(repeticao)) {

            for (int i = 0; i < totalOcorrencias; i++) {
                Consulta c = new Consulta();
                c.setPaciente(paciente);
                c.setDataHora(LocalDateTime.of(dataBase.plusWeeks(i * 4), horaBase));
                c.setGrupoRecorrencia(grupoId);
                c.setStatus("PENDENTE");
                consultaRepository.save(c);
            }

        } else {

            Consulta c = new Consulta();
            c.setPaciente(paciente);
            c.setDataHora(LocalDateTime.of(dataBase, horaBase));
            c.setGrupoRecorrencia(null);
            c.setStatus("PENDENTE");
            consultaRepository.save(c);
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping
    public List<Consulta> listarConsultas() {
        return consultaRepository.findAll();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarUm(@PathVariable Long id) {
        consultaRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/grupo/{grupoId}")
    public ResponseEntity<?> deletarGrupo(@PathVariable String grupoId) {
        consultaRepository.deleteByGrupoRecorrencia(grupoId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarConsulta(@PathVariable Long id, @RequestBody Map<String, Object> dados) {

        Consulta consulta = consultaRepository.findById(id).orElse(null);

        if (consulta == null) {
            return ResponseEntity.notFound().build();
        }

        if (dados.containsKey("observacao")) {
            consulta.setObservacao((String) dados.get("observacao"));
        }

        if (dados.containsKey("dataHora")) {
            consulta.setDataHora(LocalDateTime.parse(dados.get("dataHora").toString()));
        }

        if (dados.containsKey("status")) {
            consulta.setStatus(dados.get("status").toString());
        }

        consultaRepository.save(consulta);

        return ResponseEntity.ok().build();
    }
}