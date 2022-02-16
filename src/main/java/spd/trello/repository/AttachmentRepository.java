package spd.trello.repository;

import org.springframework.stereotype.Repository;
import spd.trello.domain.Attachment;

import java.util.List;
import java.util.UUID;

@Repository
public interface AttachmentRepository extends AbstractRepository<Attachment> {
    List<Attachment> findAllByCommentId(UUID commentId);

    List<Attachment> findAllByCardId(UUID cardId);
}
