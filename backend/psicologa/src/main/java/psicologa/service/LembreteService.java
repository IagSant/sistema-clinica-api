package psicologa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import psicologa.model.Consulta;
import psicologa.repository.ConsultaRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class LembreteService {

    @Autowired
    private ConsultaRepository consultaRepository;

    @Autowired
    private TwilioService twilioService;

    @Scheduled(fixedRate = 10000) // depois tenho que mudar pra cron
    public void enviarLembretes() {

        System.out.println("AGENDADOR RODANDO...");

        LocalDate hoje = LocalDate.now();
        LocalDate limite = hoje.plusDays(7);

        List<Consulta> consultas = consultaRepository.findAll();

        for (Consulta c : consultas) {

            if (c.isLembreteEnviado()) continue;

            LocalDate dataConsulta = c.getDataHora().toLocalDate();

            if (!dataConsulta.isBefore(hoje) && !dataConsulta.isAfter(limite)) {

                String nome = c.getPaciente().getNome();
                String telefone = c.getPaciente().getTelefone();

                String link = "http://localhost:8080/confirmacao?id=" + c.getId();

                String mensagem = "Olá " + nome + ", tudo bem?\n\n"
                        + "Estou passando para confirmar sua consulta 🧠✨\n\n"
                        + "📅 Data: " + c.getDataHora() + "\n\n"
                        + "👉 Clique abaixo para confirmar ou cancelar:\n"
                        + link;

                System.out.println("📲 Enviando para: " + telefone);

                twilioService.enviarMensagem(telefone, mensagem);

                c.setLembreteEnviado(true);
                consultaRepository.save(c);
            }
        }
    }
}