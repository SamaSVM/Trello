package spd.trello;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spd.trello.domain.Reminder;
import spd.trello.repository.ReminderRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Component
@EnableScheduling
public class ReminderScheduler {
    private final ReminderRepository repository;

    @Autowired
    public ReminderScheduler(ReminderRepository repository) {
        this.repository = repository;
    }

    @Scheduled(cron = "0 0/5 * * * ?")//every 5 min
    public void runReminder() {
        log.info("Run ReminderScheduler at - {}", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        List<Reminder> activeReminders =
                repository.getAllByRemindOnBeforeAndActive(LocalDateTime.now(), true);
        activeReminders.forEach(reminder -> {
            System.out.println("Hallo! Wake Up! " + reminder.getId());
            log.debug("Reminder sent for {}", reminder.getCreatedBy());
            reminder.setActive(false);
            repository.save(reminder);
        });
    }
}
