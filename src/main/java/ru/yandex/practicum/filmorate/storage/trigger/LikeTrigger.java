package ru.yandex.practicum.filmorate.storage.trigger;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.TriggerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class LikeTrigger extends TriggerAdapter {
    String sql = "INSERT INTO EVENTS (EVENT_TYPE,OPERATION,USER_ID,ENTITY_ID,EVENT_TS) " +
            "VALUES ('LIKE',?,?,?,CURRENT_TIMESTAMP)";

    @Override
    public void fire(Connection connection, ResultSet oldRow, ResultSet newRow) throws SQLException {
        if (newRow != null && newRow.next()) {
            // INSERT ROW
            long filmId = newRow.getLong("FILM_ID");
            long userId = newRow.getLong("USER_ID");
            log.info("LikeTrigger: ADD {} {}", userId, filmId);
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, "ADD");
            ps.setLong(2, userId);
            ps.setLong(3, filmId);
            ps.executeUpdate();
            ps.close();
        }
        if (oldRow != null && oldRow.next()) {
            // DELETE ROW
            long filmId = oldRow.getLong("FILM_ID");
            long userId = oldRow.getLong("USER_ID");
            log.info("LikeTrigger: DEL {} {}", userId, filmId);
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, "REMOVE");
            ps.setLong(2, userId);
            ps.setLong(3, filmId);
            ps.executeUpdate();
            ps.close();
        }
    }
}
