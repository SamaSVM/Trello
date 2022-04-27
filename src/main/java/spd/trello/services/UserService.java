package spd.trello.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spd.trello.domain.User;
import spd.trello.repository.UserRepository;
import spd.trello.validators.UserValidator;

import java.lang.module.ResolutionException;
import java.util.Locale;
import java.util.UUID;

@Service
public class UserService extends AbstractService<User, UserRepository, UserValidator> {

    private final MemberService memberService;

    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository repository, UserValidator validator, MemberService memberService,
                       PasswordEncoder passwordEncoder) {
        super(repository, validator);
        this.memberService = memberService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User save(User entity) {
        if (entity.getEmail() != null) {
            entity.setEmail(entity.getEmail().toLowerCase(Locale.ROOT));
        }
        return super.save(entity);
    }

    @Override
    public User update(User entity) {
        if (entity.getEmail() != null) {
            entity.setEmail(entity.getEmail().toLowerCase(Locale.ROOT));
        }
        return super.update(entity);
    }

    @Override
    public void delete(UUID id) {
        memberService.deleteMembersForUser(id);
        super.delete(id);
    }

    public User register(User user) {
        encodePassword(user);
        return repository.save(user);
    }

    public User findByEmail(String email) {
        return repository.findByEmail(email).orElseThrow(ResolutionException::new);
    }

    private void encodePassword(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
    }
}
