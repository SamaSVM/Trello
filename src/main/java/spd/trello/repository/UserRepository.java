package spd.trello.repository;

import org.springframework.stereotype.Repository;
import spd.trello.domain.User;

@Repository
public interface UserRepository extends AbstractRepository<User> {
}
