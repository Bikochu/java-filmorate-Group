package ru.yandex.practicum.filmorate.storage.trigger;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.TriggerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class FriendTrigger extends TriggerAdapter {
    String sql = "INSERT INTO EVENTS (EVENT_TYPE,OPERATION,USER_ID,ENTITY_ID,EVENT_TS) "+
            "VALUES ('FRIEND',?,?,?,CURRENT_TIMESTAMP)";

    @Override
    public void fire(Connection connection, ResultSet oldRow, ResultSet newRow) throws SQLException {
        if(newRow!=null && newRow.next()) {
            // INSERT ROW
            long friendId = newRow.getLong("FRIEND_ID");
            long userId = newRow.getLong("USER_ID");
            log.info("FriendTrigger: ADD {} {}",userId, friendId);
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, "ADD");
            ps.setLong(2, userId);
            ps.setLong(3, friendId);
            ps.executeUpdate();
            ps.close();
        }
        if(oldRow!=null && oldRow.next()) {
            // DELETE ROW
            long friendId = oldRow.getLong("FRIEND_ID");
            long userId = oldRow.getLong("USER_ID");
            log.info("FriendTrigger: DEL {} {}",userId, friendId);
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, "REMOVE");
            ps.setLong(2, userId);
            ps.setLong(3, friendId);
            ps.executeUpdate();
            ps.close();
        }
    }
}
