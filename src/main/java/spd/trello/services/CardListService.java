package spd.trello.services;

import spd.trello.domain.Card;
import spd.trello.domain.CardList;
import spd.trello.domain.Member;
import spd.trello.domain.User;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.InterfaceRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class CardListService extends AbstractService<CardList>{
    public CardListService(InterfaceRepository<CardList> repository) {
        super(repository);
    }

    public CardList findById(UUID id) {
        return repository.findById(id);
    }

    public List<CardList> findAll() {
        return repository.findAll();
    }

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
        if (member.getMemberRole() == MemberRole.GUEST) {
            throw new IllegalStateException("This user cannot update workspace!");
        }
        entity.setUpdatedBy(member.getCreatedBy());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        return repository.update(entity);
    }

    public boolean delete(UUID id) {
        return repository.delete(id);
    }
}
