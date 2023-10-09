package ru.practicum.util;

import dto.*;
import ru.practicum.model.Category;
import ru.practicum.model.Event;

public interface EventMapper {

    Event toEvent(NewEventDto dto, Category category);

    Event toEvent(UpdateEventUserRequest dto, Category category, Integer eventId);

    Event toEvent(UpdateEventAdminRequest dto, Category category, Integer eventId);

    EventFullDto toDto(Event event, Integer confirmedRequests);

    EventShortDto toShortDto(Event event, Integer confirmedRequests);

}