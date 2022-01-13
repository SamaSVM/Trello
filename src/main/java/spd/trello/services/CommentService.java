package spd.trello.services;

import spd.trello.domain.Comment;
import spd.trello.domain.Member;
import spd.trello.domain.enums.MemberRole;
import spd.trello.repository.InterfaceRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.UUID;

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
        if (member.getMemberRole() == MemberRole.GUEST) {
            throw new IllegalStateException("This user cannot update comment!");
        }
        Comment oldCard = repository.findById(entity.getId());
        entity.setUpdatedBy(member.getCreatedBy());
        entity.setUpdatedDate(Date.valueOf(LocalDate.now()));
        if (entity.getText() == null) {
            entity.setText(oldCard.getText());
        }
        return repository.update(entity);
    }
}
