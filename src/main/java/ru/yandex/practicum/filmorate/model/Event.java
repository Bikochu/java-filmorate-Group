package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.Objects;

@Data
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {
    long eventId;
    String eventType; // LIKE, REVIEW, FRIEND
    String operation; // REMOVE, ADD, UPDATE
    long userId;
    long entityId;
    long timestamp;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return eventId == event.eventId && userId == event.userId && entityId == event.entityId && timestamp == event.timestamp && Objects.equals(eventType, event.eventType) && Objects.equals(operation, event.operation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, eventType, operation, userId, entityId, timestamp);
    }
}
