package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Member;
import spd.trello.domain.User;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.InterfaceRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class MemberService extends AbstractService<Member> {
    public MemberService(InterfaceRepository<Member> repository) {
        super(repository);
    }

    public Member findById(UUID id) {
        return repository.findById(id);
    }

    public List<Member> findAll() {
        return repository.findAll();
    }

    public Member create(User user, MemberRole memberRole) {
        Member member = new Member();
        member.setId(UUID.randomUUID());
        member.setCreatedBy(user.getEmail());
        member.setCreatedDate(Date.valueOf(LocalDate.now()));
        member.setUserId(user.getId());
        if (memberRole != null){
            member.setMemberRole(memberRole);
        }
        repository.create(member);
        return repository.findById(member.getId());
    }

    public Member update(User user, Member entity) {
        entity.setUpdatedBy(user.getEmail());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        return repository.update(entity);
    }

    public boolean delete(UUID id) {
        return repository.delete(id);
    }
}
