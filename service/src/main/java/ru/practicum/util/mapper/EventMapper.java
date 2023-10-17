package ru.practicum.util.mapper;

import dto.*;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.User;

public interface EventMapper {

    Event toEvent(NewEventDto dto, Category category, User user, Location location);

    Event toEvent(UpdateEventUserRequest dto, Category category, Integer eventId);

    Event toEvent(UpdateEventAdminRequest dto, Category category, Integer eventId);

    EventFullDto toDto(Event event);

    EventShortDto toShortDto(Event event);
}