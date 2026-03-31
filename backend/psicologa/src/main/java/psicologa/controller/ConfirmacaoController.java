package psicologa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import psicologa.model.Consulta;
import psicologa.repository.ConsultaRepository;

@CrossOrigin(origins = "*") // 🔥 AQUI
@RestController
@RequestMapping("/api")
public class ConfirmacaoController {

    @Autowired
    private ConsultaRepository consultaRepository;

    @GetMapping(value = "/consulta", produces = MediaType.APPLICATION_JSON_VALUE)
    public Consulta buscar(@RequestParam Long id) {
        return consultaRepository.findById(id).orElse(null);
    }

    @PostMapping("/confirmar")
    public String confirmar(@RequestParam Long id) {

        Consulta c = consultaRepository.findById(id).orElse(null);

        if (c != null) {

            if ("CONFIRMADO".equals(c.getStatus())) {
                return "já confirmado";
            }

            c.setStatus("CONFIRMADO");
            consultaRepository.save(c);
        }

        return "ok";
    }

    @PostMapping("/cancelar")
    public String cancelar(@RequestParam Long id) {

        Consulta c = consultaRepository.findById(id).orElse(null);

        if (c != null) {

            if ("CONFIRMADO".equals(c.getStatus())) {
                return "já confirmado";
            }

            c.setStatus("CANCELADO");
            consultaRepository.save(c);
        }

        return "ok";
    }
}