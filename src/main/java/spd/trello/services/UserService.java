package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.User;
import spd.trello.exeption.BadRequestException;
import spd.trello.repository.UserRepository;

import java.time.ZoneId;
import java.util.UUID;

@Service
public class UserService extends AbstractService<User, UserRepository> {

    public UserService(UserRepository repository, MemberService memberService) {
        super(repository);
        this.memberService = memberService;
    }
    private final MemberService memberService;

    @Override
    public User save(User entity) {
        if(entity.getTimeZone() == null){
            entity.setTimeZone(ZoneId.systemDefault().toString());
        }

        try {
            return repository.save(entity);
        }catch (RuntimeException e){
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public User update(User entity) {
        User oldUser = getById(entity.getId());
        entity.setEmail(oldUser.getEmail());
        if (entity.getFirstName() == null) {
            entity.setFirstName(oldUser.getFirstName());
        }
        if (entity.getLastName() == null) {
            entity.setLastName(oldUser.getLastName());
        }
        if (entity.getTimeZone() == null) {
            entity.setTimeZone(oldUser.getTimeZone());
        }

        try {
            return repository.save(entity);
        }catch (RuntimeException e){
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public void delete(UUID id) {
        memberService.deleteMembersForUser(id);
        super.delete(id);
    }
}
