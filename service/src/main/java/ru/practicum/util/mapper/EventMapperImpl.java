package ru.practicum.util.mapper;

import dto.EventFullDto;
import dto.EventShortDto;
import dto.LocationDto;
import dto.NewEventDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.User;

import java.time.LocalDateTime;

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
            event.setPaid(dto.getPaid() != null ? dto.getPaid() : false);
            event.setParticipantLimit(dto.getParticipantLimit() != null ? dto.getParticipantLimit() : 0);
            event.setLocation(location);
            event.setRequestModeration(dto.getRequestModeration() != null ? dto.getRequestModeration() : true);
            event.setTitle(dto.getTitle());
            event.setCreatedOn(LocalDateTime.now());
            event.setInitiator(user);
            event.setViews(0);
        }

        return event;
    }

    @Override
    public EventFullDto toDto(Event event, Integer eventLikes) {
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
        dto.setLocation(LocationMapper.toLocationDto(event.getLocation()));
        dto.setPaid(event.getPaid());
        dto.setParticipantLimit(event.getParticipantLimit());
        dto.setPublishedOn(event.getPublishedOn());
        dto.setRequestModeration(event.getRequestModeration());
        dto.setState(event.getStateEnum());
        dto.setTitle(event.getTitle());
        dto.setViews(event.getViews() + 1);
        dto.setRating(eventLikes != null ? eventLikes : 0);
        return dto;
    }

    @Override
    public EventShortDto toShortDto(Event event, Integer eventLikes) {
        EventShortDto dto = new EventShortDto();

        dto.setAnnotation(event.getAnnotation());
        dto.setDescription(event.getDescription());
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
        dto.setRating(eventLikes != null ? eventLikes : 0);
        dto.setLocation(new LocationDto(event.getLocation().getLat(), event.getLocation().getLon()));

        return dto;
    }
}
