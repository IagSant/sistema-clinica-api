package psicologa.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class ContatoController {

    @GetMapping("/")
    public String home() {
        return "API funcionando 🚀";
    }
}