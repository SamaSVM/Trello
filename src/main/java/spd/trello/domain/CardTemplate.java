package spd.trello.domain;

import lombok.Data;

@Data
public class CardTemplate extends Resource {
    private String title;

    @Override
    public String toString() {
        return "CardTemplate{" +
                "id=" + super.getId() +
                ", createdBy=" + super.getCreatedBy() +
                ", updatedBy=" + super.getUpdatedBy() +
                ", cratedDate=" + super.getCratedDate() +
                ", updatedDate=" + super.getUpdatedDate() +
                ", title='" + title + '\'' +
                '}';
    }
}
