package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Member;
import spd.trello.repository.MemberCardRepository;

import java.util.List;
import java.util.UUID;

@Service
public class MemberCardService{
    private final MemberCardRepository repository;

    public MemberCardService(MemberCardRepository repository) {
        this.repository = repository;
    }

    public boolean findByIds(UUID memberId, UUID cardId) {
        return repository.findByIds(memberId, cardId);
    }

    public List<Member> findMembersByCardId(UUID cardId) {
        return repository.findMembersByCardId(cardId);
    }

    public boolean create(UUID memberId, UUID cardId) {
        return repository.create(memberId, cardId);
    }

    public boolean delete(UUID memberId, UUID cardId) {
        return repository.delete(memberId, cardId);
    }

    public boolean deleteAllMembersForCard(UUID cardId) {
        return repository.deleteAllMembersForCard(cardId);
    }
}
