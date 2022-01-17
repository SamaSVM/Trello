package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import spd.trello.domain.perent.Resource;

@Data
@EqualsAndHashCode(callSuper=false)
public class CardTemplate extends Resource {
    private String title;

    @Override
    public String toString() {
        return "CardTemplate{" +
                "id=" + super.getId() +
                ", createdBy=" + super.getCreatedBy() +
                ", updatedBy=" + super.getUpdatedBy() +
                ", cratedDate=" + super.getCreatedDate() +
                ", updatedDate=" + super.getUpdatedDate() +
                ", title='" + title + '\'' +
                '}';
    }
}
