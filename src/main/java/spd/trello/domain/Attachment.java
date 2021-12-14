package spd.trello.domain;

import lombok.Data;

import java.io.File;

@Data
public class Attachment extends Resource {
    private String name;
    private String link;
    private File file;

    @Override
    public String toString() {
        return "Attachment{" +
                "id=" + id +
                ", createdBy=" + createdBy +
                ", updatedBy=" + updatedBy +
                ", cratedDate=" + cratedDate +
                ", updatedDate=" + updatedDate +
                ", name='" + name + '\'' +
                ", link='" + link + '\'' +
                ", file=" + file +
                '}';
    }
}
