package spd.trello.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import spd.trello.domain.perent.Resource;

import java.io.File;

@Data
@EqualsAndHashCode(callSuper=false)
public class Attachment extends Resource {
    private String name;
    private String link;
    private File file;

    @Override
    public String toString() {
        return "Attachment{" +
                "id=" + super.getId() +
                ", createdBy=" + super.getCreatedBy() +
                ", updatedBy=" + super.getUpdatedBy() +
                ", cratedDate=" + super.getCreatedDate() +
                ", updatedDate=" + super.getUpdatedDate() +
                ", name='" + name + '\'' +
                ", link='" + link + '\'' +
                ", file=" + file +
                '}';
    }
}
