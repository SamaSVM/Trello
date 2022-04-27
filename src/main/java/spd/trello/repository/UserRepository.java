package spd.trello.repository;

import org.springframework.stereotype.Repository;
import spd.trello.domain.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends AbstractRepository<User> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
