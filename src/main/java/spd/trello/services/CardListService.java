package spd.trello.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spd.trello.domain.Card;
import spd.trello.domain.CardList;
import spd.trello.domain.Member;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.InterfaceRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class CardListService extends AbstractService<CardList> {
    public CardListService(InterfaceRepository<CardList> repository) {
        super(repository);
    }

    @Autowired
    CardCardListService cardCardListService;

    public CardList create(Member member, UUID boardId, String name) {
        CardList cardList = new CardList();
        cardList.setId(UUID.randomUUID());
        cardList.setCreatedBy(member.getCreatedBy());
        cardList.setCreatedDate(Date.valueOf(LocalDate.now()));
        cardList.setName(name);
        cardList.setBoardId(boardId);
        repository.create(cardList);
        return repository.findById(cardList.getId());
    }

    public CardList update(Member member, CardList entity) {
        checkMember(member);
        CardList oldCardList = findById(entity.getId());
        entity.setUpdatedBy(member.getCreatedBy());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        if (entity.getName() == null) {
            entity.setName(oldCardList.getName());
        }
        return repository.update(entity);
    }

    public List<Card> getAllCards(Member member, UUID cardListId) {
        checkMember(member);
        return cardCardListService.getAllCardsForCardList(cardListId);
    }

    private void checkMember(Member member) {
        if (member.getMemberRole() == MemberRole.GUEST) {
            throw new IllegalStateException("This member cannot update cardList!");
        }
    }
}
