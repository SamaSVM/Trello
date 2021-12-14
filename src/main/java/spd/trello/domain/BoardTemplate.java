package spd.trello.domain;

import lombok.Data;

@Data
public class BoardTemplate extends Resource {
    private String name;

    @Override
    public String toString() {
        return "BoardTemplate{" +
                "id=" + id +
                ", createdBy=" + createdBy +
                ", updatedBy=" + updatedBy +
                ", cratedDate=" + cratedDate +
                ", updatedDate=" + updatedDate +
                ", name='" + name + '\'' +
                '}';
    }
}
