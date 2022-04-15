package spd.trello.domain.perent;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.Getter;
import lombok.Setter;
import spd.trello.validators.annotation.Present;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public class Resource extends Domain {
    @Column(name = "created_by")
    @NotNull(message = "The createdBy field must be filled.")
    @Size(min = 2, max = 30, message = "CreatedBy should be between 2 and 30 characters!")
    private String createdBy;

    @Column(name = "updated_by")
    @Size(min = 2, max = 30, message = "UpdatedBy should be between 2 and 30 characters!")
    private String updatedBy;

    @Column(name = "created_date")
    @NotNull(message = "The createdData field must be filled.")
    @Present(message = "CreateDate should not be past or future.")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdDate;

    @Column(name = "updated_date")
    @Present(message = "UpdateDate should not be past or future.")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime updatedDate;
}
