package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.*;
import spd.trello.repository.*;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class CardService extends AbstractService<Card> {
    public CardService(
            InterfaceRepository<Card> repository,
            MemberCardService memberCardService,
            CardCardListService cardCardListService
    ) {
        super(repository);
        this.memberCardService = memberCardService;
        this.cardCardListService = cardCardListService;
    }

    private final MemberCardService memberCardService;
    private final CardCardListService cardCardListService;

    public Card create(Card entity) {
        Card card = new Card();
        card.setId(UUID.randomUUID());
        card.setCreatedBy(entity.getCreatedBy());
        card.setCreatedDate(Date.valueOf(LocalDate.now()));
        card.setName(entity.getName());
        card.setDescription(entity.getDescription());
        card.setCardListId(entity.getCardListId());
        repository.create(card);
        return repository.findById(card.getId());
    }

    public Card update(Card entity) {
        Card oldCard = repository.findById(entity.getId());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        if (entity.getName() == null) {
            entity.setName(oldCard.getName());
        }
        if (entity.getDescription() == null && oldCard.getDescription() != null) {
            entity.setDescription(oldCard.getDescription());
        }
        return repository.update(entity);
    }

    public boolean addMember(UUID newMemberId, UUID cardId) {
        return memberCardService.create(newMemberId, cardId);
    }

    public boolean deleteMember(UUID memberId, UUID cardId) {
        return memberCardService.delete(memberId, cardId);
    }

    public List<Card> getAllCardsForCardList(UUID cardListId) {
        return cardCardListService.getAllCardsForCardList(cardListId);
    }
}
