package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.ReminderScheduler;
import spd.trello.domain.Card;
import spd.trello.exeption.BadRequestException;
import spd.trello.exeption.ResourceNotFoundException;
import spd.trello.repository.CardRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class CardService extends AbstractService<Card, CardRepository> {
    public CardService(CardRepository repository, ChecklistService checklistService, LabelService labelService,
                       CommentService commentService, AttachmentService attachmentService, ReminderScheduler reminderScheduler) {
        super(repository);
        this.checklistService = checklistService;
        this.labelService = labelService;
        this.commentService = commentService;
        this.attachmentService = attachmentService;
        this.reminderScheduler = reminderScheduler;
    }

    private final ChecklistService checklistService;
    private final LabelService labelService;
    private final CommentService commentService;
    private final AttachmentService attachmentService;
    private final ReminderScheduler reminderScheduler;


    @Override
    public Card save(Card entity) {
        entity.setCreatedDate(LocalDateTime.now());

        try {
            Card card = repository.save(entity);
            if(card.getReminder().getActive()){
                reminderScheduler.addReminder(entity.getReminder());
            }
            return card;
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public Card update(Card entity) {
        Card oldCard = getById(entity.getId());

        if (entity.getUpdatedBy() == null) {
            throw new BadRequestException("Not found updated by!");
        }

        if (entity.getName() == null && entity.getDescription() == null && entity.getArchived() == oldCard.getArchived()
                && entity.getMembersId().equals(oldCard.getMembersId())) {
            throw new ResourceNotFoundException();
        }

        if(oldCard.getReminder().getActive() && !entity.getReminder().getActive()){
            reminderScheduler.deleteReminder(oldCard.getReminder());
        }

        entity.setCardListId(oldCard.getCardListId());
        entity.setUpdatedDate(LocalDateTime.now());
        entity.setCreatedBy(oldCard.getCreatedBy());
        entity.setCreatedDate(oldCard.getCreatedDate());
        if (entity.getName() == null) {
            entity.setName(oldCard.getName());
        }
        if (entity.getDescription() == null && oldCard.getDescription() != null) {
            entity.setDescription(oldCard.getDescription());
        }
        try {
            Card card = repository.save(entity);
            if (card.getReminder().getActive() && !oldCard.getReminder().equals(card.getReminder())) {
                reminderScheduler.addReminder(card.getReminder());
            }
            return card;
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public void delete(UUID id) {
        checklistService.deleteCheckListsForCard(id);
        labelService.deleteLabelsForCard(id);
        commentService.deleteCommentsForCard(id);
        attachmentService.deleteAttachmentsForCard(id);
        super.delete(id);
    }

    public void deleteMemberInCards(UUID memberId) {
        List<Card> cards = repository.findAllBymembersIdEquals(memberId);
        for (Card card : cards) {
            Set<UUID> membersId = card.getMembersId();
            membersId.remove(memberId);
            if (card.getMembersId().isEmpty()) {
                delete(card.getId());
            }
        }
    }

    public void runReminder() {
        reminderScheduler.runReminder();
    }

    public void deleteCardsForCardList(UUID cardListId) {
        repository.findAllByCardListId(cardListId).forEach(card -> delete(card.getId()));
    }
}
