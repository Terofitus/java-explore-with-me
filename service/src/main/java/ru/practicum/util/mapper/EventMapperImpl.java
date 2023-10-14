package ru.practicum.util.mapper;

import dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class EventMapperImpl implements EventMapper {

    @Override
    public Event toEvent(NewEventDto dto, Category category, User user, Location location) {
        if (dto == null && category == null) {
            return null;
        }

        Event event = new Event();

        if (dto != null) {
            event.setAnnotation(dto.getAnnotation());
            event.setCategory(category);
            event.setDescription(dto.getDescription());
            event.setEventDate(dto.getEventDate());
            event.setPaid(dto.getPaid());
            event.setParticipantLimit(dto.getParticipantLimit());
            event.setLocation(location);
            event.setRequestModeration(dto.getRequestModeration());
            event.setTitle(dto.getTitle());
            event.setCreatedOn(LocalDateTime.now());
            event.setInitiator(user);
            event.setViews(0);
        }

        return event;
    }


    @Override
    public Event toEvent(UpdateEventUserRequest dto, Category category, Integer eventId) {
        if (dto == null && category == null) {
            return null;
        }

        Event event = new Event();

        if (dto != null) {
            event.setId(eventId);
            if (dto.getEventDate() != null) {
                event.setEventDate(dto.getEventDate());
            }
            event.setTitle(dto.getTitle());
            event.setAnnotation(dto.getAnnotation());
            event.setPaid(dto.getPaid());
            event.setDescription(dto.getDescription());
            event.setParticipantLimit(dto.getParticipantLimit());
            if (category != null) {
                event.setCategory(category);
            }
        }

        return event;
    }

    @Override
    public Event toEvent(UpdateEventAdminRequest dto, Category category, Integer eventId) {
        if (dto == null && category == null) {
            return null;
        }

        Event event = new Event();
        if (Objects.requireNonNull(dto).getAnnotation() != null) {
            event.setAnnotation(dto.getAnnotation());
        }
        event.setCategory(category);
        if (dto.getEventDate() != null) {
            event.setEventDate(dto.getEventDate());
        }
        event.setDescription(dto.getDescription());
        if (dto.getLocation() != null) {
            event.setLocation(LocationMapper.toLocation(dto.getLocation()));
        }
        event.setPaid(dto.getPaid());
        event.setParticipantLimit(dto.getParticipantLimit());
        event.setRequestModeration(dto.getRequestModeration());
        event.setTitle(dto.getTitle());

        return event;
    }

    @Override
    public EventFullDto toDto(Event event) {
        EventFullDto dto = new EventFullDto();

        dto.setAnnotation(event.getAnnotation());
        dto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        dto.setConfirmedRequests(event.getParticipants().size());
        if (event.getCreatedOn() != null) {
            dto.setCreatedOn(event.getCreatedOn());
        }
        dto.setDescription(event.getDescription());
        if (event.getEventDate() != null) {
            dto.setEventDate(event.getEventDate());
        }
        dto.setId(event.getId());
        dto.setInitiator(UserMapper.toUserShortDto((event.getInitiator())));
        dto.setLocationDto(LocationMapper.toLocationDto(event.getLocation()));
        dto.setPaid(event.getPaid());
        dto.setParticipantLimit(event.getParticipantLimit());
        dto.setPublishedOn(event.getPublishedOn());
        dto.setRequestModeration(event.getRequestModeration());
        dto.setState(event.getStateEnum());
        dto.setTitle(event.getTitle());
        dto.setViews(event.getViews());
        return dto;
    }

    @Override
    public EventShortDto toShortDto(Event event) {
        EventShortDto dto = new EventShortDto();

        dto.setAnnotation(event.getAnnotation());
        dto.setCategory(CategoryMapper.toCategoryDto(event.getCategory()));
        dto.setConfirmedRequests(event.getParticipants().size());
        if (event.getEventDate() != null) {
            dto.setEventDate(event.getEventDate());
        }
        dto.setId(event.getId());
        dto.setInitiator(UserMapper.toUserShortDto(event.getInitiator()));
        dto.setPaid(event.getPaid());
        dto.setTitle(event.getTitle());
        dto.setViews(event.getViews());

        return dto;
    }
}
