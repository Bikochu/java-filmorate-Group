package ru.yandex.practicum.filmorate.storage.trigger;

import lombok.extern.slf4j.Slf4j;
import org.h2.tools.TriggerAdapter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

@Slf4j
public class ReviewTrigger extends TriggerAdapter {
    String sql = "INSERT INTO EVENTS (EVENT_TYPE,OPERATION,USER_ID,ENTITY_ID,EVENT_TS) " +
            "VALUES ('REVIEW',?,?,?,CURRENT_TIMESTAMP)";
    String upd = "UPDATE EVENTS SET EVENT_TS=CURRENT_TIMESTAMP WHERE " +
            " EVENT_TYPE='REVIEW' AND OPERATION='UPDATE' AND USER_ID=? AND ENTITY_ID=?";

    private void logEvent(Connection connection, String op, long userId, long entityId) throws SQLException {
        log.info("ReviewTrigger: {} {} {}", op, userId, entityId);
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setString(1, op);
        ps.setLong(2, userId);
        ps.setLong(3, entityId);
        int cnt = ps.executeUpdate();
        ps.close();
    }

    @Override
    public void fire(Connection connection, ResultSet oldRow, ResultSet newRow) throws SQLException {
        if (newRow != null && newRow.next()) {
            // INSERT ROW
            long reviewId = newRow.getLong("REVIEW_ID");
            long userId = newRow.getLong("USER_ID");
            if (oldRow != null && oldRow.next()) {
                String oldContent = oldRow.getString("content");
                boolean oldPositive = oldRow.getBoolean("positive");
                String newContent = newRow.getString("content");
                boolean newPositive = newRow.getBoolean("positive");

                //log.info("oldContent={} oldPositive={}",oldContent,oldPositive);
                //log.info("newContent={} newPositive={}",newContent,newPositive);

                if (Objects.equals(newContent, oldContent) && oldPositive == newPositive) {
                    // nothing to change - skip
                    log.info("nothing to change - skip");
                    return;
                }

                PreparedStatement ps = connection.prepareStatement(upd);
                ps.setLong(1, userId);
                ps.setLong(2, reviewId);
                int cnt = ps.executeUpdate();
                ps.close();
                if (cnt > 0) {
                    log.info("ReviewTrigger: {} {} {}", "RENEW UPDATE", userId, reviewId);
                } else {
                    logEvent(connection, "UPDATE", userId, reviewId);
                }
            } else {
                logEvent(connection, "ADD", userId, reviewId);
            }
        }
        if (oldRow != null && oldRow.next()) {
            // DELETE ROW
            long reviewId = oldRow.getLong("REVIEW_ID");
            long userId = oldRow.getLong("USER_ID");
            logEvent(connection, "REMOVE", userId, reviewId);
        }
    }
}
