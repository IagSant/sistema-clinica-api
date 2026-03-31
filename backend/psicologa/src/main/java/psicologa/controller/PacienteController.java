package psicologa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import psicologa.model.Paciente;
import psicologa.repository.PacienteRepository;

import java.util.List;

@RestController
@RequestMapping("/api/pacientes")
@CrossOrigin(origins = "*")
public class PacienteController {

    @Autowired
    private PacienteRepository repository;

    // LISTAR
    @GetMapping(produces = "application/json") // 🔥 fica só esse
    public List<Paciente> listar() {
        return repository.findAll();
    }

    // SALVAR
    @PostMapping
    public Paciente salvar(@RequestBody Paciente paciente) {
        return repository.save(paciente);
    }

    // DELETAR
    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        repository.deleteById(id);
    }

    // ATUALIZAR
    @PutMapping("/{id}")
    public Paciente atualizar(@PathVariable Long id, @RequestBody Paciente paciente) {
        paciente.setId(id);
        return repository.save(paciente);
    }
}