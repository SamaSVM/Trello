package spd.trello.domain;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import spd.trello.domain.perent.Domain;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "files")
public class FileDB extends Domain {
    private String name;
    private String type;
    @Lob
    private byte[] data;
}
