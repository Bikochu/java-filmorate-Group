package ru.yandex.practicum.filmorate.storage.event;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {
    Event getEventById(long eventId);

    List<Event> getAllEvents();

    List<Event> getAllEventsByUser(long userId);

    Event createEvent(String type, String operation, long userId, long entityid);
}
