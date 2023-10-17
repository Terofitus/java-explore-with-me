package ru.practicum.service.event;

import dto.UpdateEventAdminRequest;
import ru.practicum.model.Event;
import ru.practicum.model.model_attribute.AdminEventSearchParam;

import java.util.List;

public interface EventServiceAdmin {
    List<Event> eventSearch(AdminEventSearchParam params);

    Event updateEvent(Integer eventId, UpdateEventAdminRequest requestBody);
}
