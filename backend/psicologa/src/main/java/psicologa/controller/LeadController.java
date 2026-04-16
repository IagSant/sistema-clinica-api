package psicologa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import psicologa.model.Lead;
import psicologa.repository.LeadRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/leads")
@CrossOrigin(origins = "*")
public class LeadController {

    @Autowired
    private LeadRepository repository;

    // CRIAR LEAD (SITE)
    @PostMapping
    public Lead criar(@RequestBody Lead lead) {
        return repository.save(lead);
    }

    // LISTAR LEADS
    @GetMapping(produces = "application/json")
    public List<Lead> listar() {
        return repository.findAll();
    }

    // MARCAR COMO ATENDIDO (PARA SLA)
    @PutMapping("/{id}")
    public Lead atualizar(@PathVariable Long id, @RequestBody Map<String, String> dados) {

        Lead lead = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lead não encontrado"));

        if ("ATENDIDO".equals(dados.get("status"))) {
            lead.setAtendidoEm(LocalDateTime.now());
            lead.setStatus("ATENDIDO");
        }

        return repository.save(lead);
    }

    // (OPCIONAL) DELETAR
    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        repository.deleteById(id);
    }

}