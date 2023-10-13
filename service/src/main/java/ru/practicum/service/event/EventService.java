package ru.practicum.service.event;

import ru.practicum.model.Event;
import ru.practicum.model.EventRequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {
    List<Event> getEvents(EventRequestParam params, HttpServletRequest request);

    Event getEventById(Integer id, HttpServletRequest request);
}
