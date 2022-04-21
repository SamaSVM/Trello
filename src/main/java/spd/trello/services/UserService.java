package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.User;
import spd.trello.repository.UserRepository;
import spd.trello.validators.UserValidator;

import java.util.Locale;
import java.util.UUID;

@Service
public class UserService extends AbstractService<User, UserRepository, UserValidator> {

    private final MemberService memberService;

    public UserService(UserRepository repository, UserValidator validator, MemberService memberService) {
        super(repository, validator);
        this.memberService = memberService;
    }

    @Override
    public User save(User entity) {
        return super.save(entity);
    }

    @Override
    public User update(User entity) {
        return super.update(entity);
    }

    @Override
    public void delete(UUID id) {
        memberService.deleteMembersForUser(id);
        super.delete(id);
    }
}
