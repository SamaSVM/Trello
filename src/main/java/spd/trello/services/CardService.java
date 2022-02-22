package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.*;
import spd.trello.exeption.BadRequestException;
import spd.trello.repository.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class CardService extends AbstractService<Card, CardRepository> {
    public CardService(CardRepository repository, ChecklistService checklistService, LabelService labelService,
                       CommentService commentService, AttachmentService attachmentService) {
        super(repository);
        this.checklistService = checklistService;
        this.labelService = labelService;
        this.commentService = commentService;
        this.attachmentService = attachmentService;
    }

    private final ChecklistService checklistService;
    private final LabelService labelService;
    private final CommentService commentService;
    private final AttachmentService attachmentService;


    @Override
    public Card save(Card entity) {
        entity.setCreatedDate(Date.valueOf(LocalDate.now()));
        try {
            return repository.save(entity);
        } catch (RuntimeException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public Card update(Card entity) {
        Card oldCard = getById(entity.getId());
        entity.setCardListId(oldCard.getCardListId());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        entity.setCreatedBy(oldCard.getCreatedBy());
        entity.setCreatedDate(oldCard.getCreatedDate());
        if (entity.getName() == null) {
            entity.setName(oldCard.getName());
        }
        if (entity.getDescription() == null && oldCard.getDescription() != null) {
            entity.setDescription(oldCard.getDescription());
        }
        try {
            return repository.save(entity);
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

    public void deleteCardsForCardList(UUID cardListId) {
        repository.findAllByCardListId(cardListId).forEach(card -> delete(card.getId()));
    }
}
