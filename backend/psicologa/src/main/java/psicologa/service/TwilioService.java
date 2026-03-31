package psicologa.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    private final String ACCOUNT_SID = System.getenv("TWILIO_SID");
    private final String AUTH_TOKEN = System.getenv("TWILIO_TOKEN");
    private final String FROM_NUMBER = System.getenv("TWILIO_WHATSAPP_NUMBER");

    public void enviarMensagem(String telefone, String mensagem) {

        if (ACCOUNT_SID == null || AUTH_TOKEN == null || FROM_NUMBER == null) {
            throw new RuntimeException("Credenciais do Twilio não configuradas.");
        }

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        Message.creator(
                new PhoneNumber("whatsapp:+55" + telefone),
                new PhoneNumber(FROM_NUMBER),
                mensagem
        ).create();
    }
}