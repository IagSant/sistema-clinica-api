package psicologa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import psicologa.service.TwilioService;

@SpringBootApplication
@EnableScheduling
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    CommandLineRunner run(TwilioService twilioService) {
        return args -> {
            try {
                twilioService.enviarMensagem("4198471657", "Teste WhatsApp 🚀");
            } catch (Exception e) {
                System.out.println("ERRO TWILIO: " + e.getMessage());
            }
        };
    }
}