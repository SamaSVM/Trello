package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Member;
import spd.trello.domain.User;
import spd.trello.repository.InterfaceRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class MemberService extends AbstractService<Member> {
    public MemberService(InterfaceRepository<Member> repository) {
        super(repository);
    }

    @Override
    public Member create(Member entity) {
        Member member = new Member();
        member.setId(UUID.randomUUID());
        member.setCreatedBy(entity.getCreatedBy());
        member.setCreatedDate(Date.valueOf(LocalDate.now()));
        member.setUserId(entity.getUserId());
        member.setMemberRole(entity.getMemberRole());
        repository.create(member);
        return repository.findById(member.getId());
    }

    public Member update(User user, Member entity) {
        Member oldMember = findById(entity.getId());
        entity.setUpdatedBy(user.getEmail());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        if (entity.getMemberRole() == null) {
            entity.setMemberRole(oldMember.getMemberRole());
        }
        return repository.update(entity);
    }
}
