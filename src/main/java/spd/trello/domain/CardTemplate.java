package spd.trello.domain;

import lombok.Data;

@Data
public class CardTemplate extends Resource{
    private String title;

    @Override
    public String toString() {
        return "CardTemplate{" +
                "id=" + id +
                ", createdBy=" + createdBy +
                ", updatedBy=" + updatedBy +
                ", cratedDate=" + cratedDate +
                ", updatedDate=" + updatedDate +
                ", title='" + title + '\'' +
                '}';
    }
}
