package spd.trello.domain;

import lombok.Data;

import java.util.List;

@Data
public class CardList extends Resource {
    private String name;
    private List<Card> cards;
    private Boolean archived = false;

    @Override
    public String toString() {
        return "CardList{" +
                "id=" + super.getId() +
                ", createdBy=" + super.getCreatedBy() +
                ", updatedBy=" + super.getUpdatedBy() +
                ", cratedDate=" + super.getCratedDate() +
                ", updatedDate=" + super.getUpdatedDate() +
                ", name='" + name + '\'' +
                ", cards=" + cards +
                ", archived=" + archived +
                '}';
    }
}
