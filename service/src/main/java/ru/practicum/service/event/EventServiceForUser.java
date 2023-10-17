package ru.practicum.service.event;

import dto.NewEventDto;
import dto.UpdateEventUserRequest;
import ru.practicum.model.Event;

import java.util.List;

public interface EventServiceForUser {
    List<Event> getEventsOfOwner(Integer userId, Integer from, Integer size);

    Event addEvent(Integer userId, NewEventDto dto);

    Event getEventForOwner(Integer userId, Integer eventId);

    Event updateEvent(Integer userId, Integer eventId, UpdateEventUserRequest dto);

}
