package ru.practicum.service.event;

import dto.LocationDto;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.model_attribute.EventRequestParam;

import java.util.List;

public interface EventService {
    List<Event> getEvents(EventRequestParam params);

    Event getEventById(Integer id);

    void addHitsToEvents(List<Event> events);

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    Location prepareLocation(LocationDto loc);
}
