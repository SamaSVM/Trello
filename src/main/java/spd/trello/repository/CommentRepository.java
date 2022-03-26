package spd.trello.repository;

import org.springframework.stereotype.Repository;
import spd.trello.domain.Comment;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends AbstractRepository<Comment> {
    List<Comment> findAllByCardId(UUID cardId);
}