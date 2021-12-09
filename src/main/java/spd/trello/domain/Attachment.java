package spd.trello.domain;

import lombok.Data;

import java.io.File;

@Data
public class Attachment {
    private String name;
    private String link;
    private File file;
}
