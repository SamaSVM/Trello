package spd.trello.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import spd.trello.domain.Card;
import spd.trello.repository.CardRepository;
import spd.trello.validators.CardValidator;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class CardService extends AbstractService<Card, CardRepository, CardValidator> {
    public CardService(CardRepository repository, CardValidator validator, ChecklistService checklistService,
                       LabelService labelService, CommentService commentService, AttachmentService attachmentService) {
        super(repository, validator);
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
    public void delete(UUID id) {
        checklistService.deleteCheckListsForCard(id);
        labelService.deleteLabelsForCard(id);
        commentService.deleteCommentsForCard(id);
        attachmentService.deleteAttachmentsForCard(id);
        super.delete(id);
    }

    public void deleteMemberInCards(UUID memberId) {
        log.debug("Cascade delete member in cards with id - {}", memberId);
        List<Card> cards = repository.findAllByMembersIdEquals(memberId);
        for (Card card : cards) {
            Set<UUID> membersId = card.getMembersId();
            membersId.remove(memberId);
            if (card.getMembersId().isEmpty()) {
                delete(card.getId());
            }
        }
    }

    public void deleteCardsForCardList(UUID cardListId) {
        log.debug("Cascade delete cards gor card lists with id - {}", cardListId);
        repository.findAllByCardListId(cardListId).forEach(card -> delete(card.getId()));
    }
}
