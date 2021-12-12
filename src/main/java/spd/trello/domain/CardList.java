package spd.trello.domain;

import lombok.Data;

import java.util.List;

@Data
public class CardList extends Resource{
    private String name;
    private List<Card> cards;
    private Boolean archived = false;

    @Override
    public String toString() {
        return "CardList{" +
                ", id=" + id +
                ", createdBy=" + createdBy +
                ", updatedBy=" + updatedBy +
                ", cratedDate=" + cratedDate +
                ", updatedDate=" + updatedDate +
                ", name='" + name + '\'' +
                ", cards=" + cards +
                ", archived=" + archived +
                '}';
    }
}
