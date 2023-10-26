package ru.practicum.util.mapper;

import dto.EventFullDto;
import dto.EventShortDto;
import dto.NewEventDto;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.User;

public interface EventMapper {

    Event toEvent(NewEventDto dto, Category category, User user, Location location);

    EventFullDto toDto(Event event, Integer eventLikes);

    EventShortDto toShortDto(Event event, Integer eventLikes);
}