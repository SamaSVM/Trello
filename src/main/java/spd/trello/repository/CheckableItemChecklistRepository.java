package spd.trello.repository;

import org.springframework.stereotype.Repository;
import spd.trello.domain.CheckableItem;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class CheckableItemChecklistRepository {
    public CheckableItemChecklistRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private final DataSource dataSource;

    private final String FIND_BY_CHECKLIST_ID_STMT = "SELECT * FROM checkable_items WHERE checklist_id=?;";

    public List<CheckableItem> findAllCheckableItemForChecklist(UUID cardId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_CHECKLIST_ID_STMT)) {
            List<CheckableItem> result = new ArrayList<>();
            statement.setObject(1, cardId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(map(resultSet));
            }
            if (!result.isEmpty()) {
                return result;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("CheckableItemChecklistRepository::findAllCheckableItemForChecklist failed", e);
        }
        throw new IllegalStateException("CheckableItem for checklist with ID: " + cardId.toString() + " doesn't exists");
    }

    private CheckableItem map(ResultSet rs) throws SQLException {
        CheckableItem checkableItem = new CheckableItem();
        checkableItem.setId(UUID.fromString(rs.getString("id")));
        checkableItem.setName(rs.getString("name"));
        checkableItem.setChecked(rs.getBoolean("checked"));
        checkableItem.setChecklistId(UUID.fromString(rs.getString("checklist_id")));
        return checkableItem;
    }
}
