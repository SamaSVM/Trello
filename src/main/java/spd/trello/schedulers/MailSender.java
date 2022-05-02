package spd.trello.schedulers;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import spd.trello.services.EmailSenderService;

import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Setter
@Component
public class MailSender implements Runnable {
    private String email;
    private final AtomicInteger sendMails = new AtomicInteger();
    private final EmailSenderService emailSenderService;

    public MailSender(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    @Override
    public void run() {
        emailSenderService.sendMail(email, "Trello", "Hallo");
        sendMails.incrementAndGet();
    }
}


