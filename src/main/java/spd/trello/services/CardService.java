package spd.trello.services;

import spd.trello.domain.Card;
import spd.trello.domain.CardList;
import spd.trello.domain.Member;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.InterfaceRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class CardService extends AbstractService<Card> {
    public CardService(InterfaceRepository<Card> repository) {
        super(repository);
    }

    public Card findById(UUID id) {
        return repository.findById(id);
    }

    public List<Card> findAll() {
        return repository.findAll();
    }

    public Card create(Member member, UUID cardListId, String name, String description) {
        Card card = new Card();
        card.setId(UUID.randomUUID());
        card.setCreatedBy(member.getCreatedBy());
        card.setCreatedDate(Date.valueOf(LocalDate.now()));
        card.setName(name);
        card.setDescription(description);
        card.setCardListId(cardListId);
        repository.create(card);
        return repository.findById(card.getId());
    }

    public Card update(Member member, Card entity) {
        if (member.getMemberRole() == MemberRole.GUEST) {
            throw new IllegalStateException("This user cannot update workspace!");
        }
        Card oldCard = repository.findById(entity.getId());
        entity.setUpdatedBy(member.getCreatedBy());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        if (entity.getName() == null) {
            entity.setName(oldCard.getName());
        }
        if (entity.getDescription() == null && oldCard.getDescription() != null) {
            entity.setDescription(oldCard.getDescription());
        }
        if (entity.getArchived() == null) {
            entity.setArchived(oldCard.getArchived());
        }
        return repository.update(entity);
    }

    public boolean delete(UUID id) {
        return repository.delete(id);
    }
}
