package ru.practicum.service.event;

import dto.NewEventDto;
import dto.UpdateEventUserRequest;
import ru.practicum.model.Event;

import java.util.List;

public interface EventServiceUser {
    List<Event> getEventsOfOwner(Integer userId, Integer from, Integer size);

    Event addEvent(Integer userId, NewEventDto dto);

    Event getEventForOwner(Integer userId, Integer eventId);

    Event updateEvent(Integer userId, Integer eventId, UpdateEventUserRequest dto);

    Event addLikeEvent(Integer userId, Integer eventId);

    void deleteLikeEvent(Integer userId, Integer eventId);

    Integer getLikesForEvent(Integer eventId);

}
