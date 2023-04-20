package ru.yandex.practicum.filmorate.storage.event;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component("eventDbStorage")
@AllArgsConstructor
public class EventDbStorage implements EventStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Event getEventById(long eventId) {
        try {
            String sqlQuery = "select EVENT_ID,EVENT_TYPE,OPERATION,USER_ID,ENTITY_ID,EVENT_TS" +
                    " FROM EVENTS WHERE EVENT_ID = ?";
            return jdbcTemplate.queryForObject(sqlQuery, this::mapRowToEvent, eventId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException(String.format("Событие с Id %d не найдено", eventId));
        }
    }

    @Override
    public List<Event> getAllEvents() {
        String sql = "SELECT EVENT_ID,EVENT_TYPE,OPERATION,USER_ID,ENTITY_ID,EVENT_TS FROM EVENTS";
        return jdbcTemplate.query(sql, this::mapRowToEvent);
    }

    @Override
    public List<Event> getAllEventsByUser(long userId) {
        String sql = "SELECT EVENT_ID,EVENT_TYPE,OPERATION,USER_ID,ENTITY_ID,EVENT_TS" +
                " FROM EVENTS WHERE USER_ID=?";
        return jdbcTemplate.query(sql, this::mapRowToEvent, userId);
    }

    public Event createEvent(String type, String operation, long userId, long entityid) {
        String sql = "INSERT INTO EVENTS (EVENT_TYPE,OPERATION,USER_ID,ENTITY_ID,EVENT_TS) " +
                "VALUES (?,?,?,?,CURRENT_TIMESTAMP)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"event_Id"});
            stmt.setString(1, type);
            stmt.setString(2, operation);
            stmt.setLong(3, userId);
            stmt.setLong(4, entityid);
            return stmt;
        }, keyHolder);
        long id = keyHolder.getKey().longValue();
        return getEventById(id);
    }

    private Event mapRowToEvent(ResultSet resultSet, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(resultSet.getLong("event_id"))
                .eventType(resultSet.getString("EVENT_TYPE"))
                .operation(resultSet.getString("operation"))
                .userId(resultSet.getLong("user_Id"))
                .entityId(resultSet.getLong("entity_Id"))
                .timestamp(resultSet.getTimestamp("EVENT_TS").getTime())
                .build();
    }

}
