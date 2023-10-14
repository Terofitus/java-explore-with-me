package ru.practicum.service.event;

import dto.NewEventDto;
import dto.UpdateEventAdminRequest;
import dto.UpdateEventUserRequest;
import ru.practicum.model.Event;
import ru.practicum.model.model_attribute.AdminEventSearchParam;
import ru.practicum.model.model_attribute.EventRequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    List<Event> getEvents(EventRequestParam params, HttpServletRequest request);

    Event getEventById(Integer id, HttpServletRequest request);

    void addHitsToEvents(List<Event> events);

    List<Event> eventSearch(AdminEventSearchParam params);

    Event updateEvent(Integer eventId, UpdateEventAdminRequest requestBody);

    List<Event> getEventsOfOwner(Integer userId, Integer from, Integer size);

    Event addEvent(Integer userId, NewEventDto dto);

    Event getEventForOwner(Integer userId, Integer eventId);

    Event updateEvent(Integer userId, Integer eventId, UpdateEventUserRequest dto);
}
