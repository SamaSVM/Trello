package spd.trello;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import spd.trello.domain.Reminder;
import spd.trello.repository.ReminderRepository;

import java.time.LocalDateTime;
import java.util.List;

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
        List<Reminder> activeReminders =
                repository.getAllByRemindOnBeforeAndActive(LocalDateTime.now(), true);
        activeReminders.forEach(reminder -> {
            System.out.println("Hallo! Wake Up! " + reminder.getId());
            reminder.setActive(false);
            repository.save(reminder);
        });
    }
}
