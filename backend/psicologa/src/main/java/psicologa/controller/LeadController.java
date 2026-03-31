package psicologa.controller;

import org.springframework.web.bind.annotation.*;
import psicologa.model.Lead;

import java.util.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LeadController {

    private List<Lead> leads = new ArrayList<>();

    // RECEBER LEAD DO SITE
    @PostMapping("/leads")
    public void receberLead(@RequestBody Lead lead) {
        leads.add(lead);
    }

    // ENVIAR LEADS PRA AGENDA
    @GetMapping(value = "/leads", produces = "application/json")
    public List<Lead> listarLeads() {
        return leads;
    }
}