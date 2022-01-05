package spd.trello.domain.perent;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class Resource extends Domain {
    private String createdBy;
    private String updatedBy;
    private Date createdDate;
    private Date updatedDate;
}
