package spd.trello.schedulers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spd.trello.domain.Reminder;
import spd.trello.repository.ReminderRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Slf4j
@Component
@EnableScheduling
public class ReminderScheduler {
    private final ReminderRepository repository;
    private final MailSender mailSender;
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);

    @Autowired
    public ReminderScheduler(ReminderRepository repository, MailSender mailSender) {
        this.repository = repository;
        this.mailSender = mailSender;
    }

    @Scheduled(cron = "0 0/5 * * * ?")//every 5 min
    public void runReminder() {
        List<Reminder> activeReminders =
                repository.getAllByRemindOnBeforeAndActive(LocalDateTime.now(), true);

        activeReminders.forEach(reminder -> {
            mailSender.setEmail(reminder.getCreatedBy());
            executorService.submit(mailSender);
            reminder.setActive(false);
            repository.save(reminder);
        });
        log.debug("Send mails " + mailSender.getSendMails());
    }
}
