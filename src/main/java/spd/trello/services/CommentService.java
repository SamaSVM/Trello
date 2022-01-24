package spd.trello.services;

import org.springframework.stereotype.Service;
import spd.trello.domain.Comment;
import spd.trello.domain.Member;
import spd.trello.repository.InterfaceRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

@Service
public class CommentService extends AbstractService<Comment> {
    public CommentService(InterfaceRepository<Comment> repository) {
        super(repository);
    }

    public Comment create(Member member, UUID cardId, String text) {
        Comment comment = new Comment();
        comment.setId(UUID.randomUUID());
        comment.setCreatedBy(member.getCreatedBy());
        comment.setCreatedDate(Date.valueOf(LocalDate.now()));
        comment.setText(text);
        comment.setCardId(cardId);
        repository.create(comment);
        return repository.findById(comment.getId());
    }

    public Comment update(Member member, Comment entity) {
        Comment oldCard = repository.findById(entity.getId());
        if (!member.getCreatedBy().equals(oldCard.getCreatedBy())) {
            throw new IllegalStateException("This member cannot update comment!");
        }
        entity.setUpdatedBy(member.getCreatedBy());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        if (entity.getText() == null) {
            entity.setText(oldCard.getText());
        }
        return repository.update(entity);
    }
}
