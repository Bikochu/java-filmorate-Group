package ru.yandex.practicum.filmorate.storage.event;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
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
            throw new NotFoundException("Событие с Id " + eventId + " не найдено");
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
