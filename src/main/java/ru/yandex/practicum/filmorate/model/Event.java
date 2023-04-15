package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Event {
    private long eventId;
    private String eventType; // LIKE, REVIEW, FRIEND
    private String operation; // REMOVE, ADD, UPDATE
    private long userId;
    private long entityId;
    private long timestamp;
}
