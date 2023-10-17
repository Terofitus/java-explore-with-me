package ru.practicum.service.event;

import dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictArgumentException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.User;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.category.CategoryService;
import ru.practicum.util.PageableCreator;
import ru.practicum.util.mapper.EventMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventServiceForUserImpl implements EventServiceForUser {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;
    private final EventService eventService;
    private final EventMapper eventMapper;

    @Override
    public List<Event> getEventsOfOwner(Integer userId, Integer from, Integer size) {
        Pageable pageable = PageableCreator.toPageable(from, size, null);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        log.info("Запрошены события добавленные пользователем с id={}", userId);
        eventService.addHitsToEvents(events);
        return events;
    }

    @Transactional
    @Override
    public Event addEvent(Integer userId, NewEventDto dto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id=%s не найден", userId)));
        Category category = categoryRepository.findById(dto.getCategory()).orElseThrow(
                () -> new NotFoundException(String.format("Категория с id=%s не найдена", dto.getCategory())));
        Location location = eventService.prepareLocation(dto.getLocation());
        Event event = eventMapper.toEvent(dto, category, user, location);
        Event eventFromDb = eventRepository.save(event);
        log.info("Добавлено событие с id={} пользователем с id={}", eventFromDb.getId(), userId);
        return event;
    }

    @Override
    public Event getEventForOwner(Integer userId, Integer eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException(String.format("Событие с id=%s не найдено", eventId)));
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            log.warn("Попытка получения полной информации о событии не его пользователем с id={}", userId);
            throw new ConflictArgumentException("Полною информацию о событии может просматривать только его инициатор");
        }
        log.info("Запрошено событие с id={} его инициатором с id={}", eventId, userId);
        List<Event> events = new ArrayList<>();
        events.add(event);
        eventService.addHitsToEvents(events);
        return event;
    }

    @Override
    public Event updateEvent(Integer userId, Integer eventId, UpdateEventUserRequest dto) {
        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new NotFoundException(String.format("Событие с id=%s не найдено", eventId)));
        if (!Objects.equals(event.getInitiator().getId(), userId)) {
            log.warn("Попытка изменения события с id={} не его инициатором с id={}", eventId, userId);
            throw new ConflictArgumentException("Изменять событие может только его инициатор");
        }
        prepareEventForUpdateUser(event, dto);
        Event updateEvent = eventRepository.save(event);
        log.info("Обновлено событие с id={}", eventId);
        return updateEvent;
    }

    private void prepareEventForUpdateUser(Event event, UpdateEventUserRequest request) {
        if (request == null) {
            event.setStateEnum(EventState.CANCELED);
            return;
        }
        if (event.getStateEnum() == EventState.PUBLISHED) {
            throw new ConflictArgumentException("Изменять можно только неопубликованные события");
        }

        fillingEventForUpdateUser(event, request);

        String stateAction = request.getStateAction();
        if (stateAction != null) {
            ActionStateUser action = ActionStateUser.forValues(stateAction);
            if (action == null) {
                throw new IllegalArgumentException("Пользователь может назначить только эти статусы" +
                        " своим событиям: SEND_TO_REVIEW, CANCEL_REVIEW");
            }
            if (action == ActionStateUser.SEND_TO_REVIEW) {
                event.setStateEnum(EventState.PENDING);
            } else {
                event.setStateEnum(EventState.CANCELED);
            }
        }
    }

    private void fillingEventForUpdateUser(Event event, UpdateEventUserRequest request) {
        String annotation = request.getAnnotation();
        Integer categoryId = request.getCategory();
        String description = request.getDescription();
        LocalDateTime eventDate = request.getEventDate();
        LocationDto locationDto = request.getLocation();
        Boolean paid = request.getPaid();
        Integer participantLimit = request.getParticipantLimit();
        Boolean requestModeration = request.getRequestModeration();
        String title = request.getTitle();

        if (annotation != null) {
            if (annotation.length() < 20 || annotation.length() > 2000) {
                throw new IllegalArgumentException("Длина аннотации события не может быть меньше 20 и больше 7000" +
                        " символов");
            }
            event.setAnnotation(annotation);
        }

        if (categoryId != null) {
            Category category = categoryService.getCategoryById(categoryId, null);
            event.setCategory(category);
        }

        if (description != null) {
            if (description.length() < 20 || description.length() > 7000) {
                throw new IllegalArgumentException("Длина описания события не может быть меньше 20 и больше 7000" +
                        " символов");
            }
            event.setDescription(description);
        }

        if (eventDate != null) {
            if (eventDate.isAfter(LocalDateTime.now().plusHours(2L))) {
                event.setEventDate(eventDate);
            } else {
                throw new ConflictArgumentException("Дата и время проведения события должна быть не ранее чем " +
                        LocalDateTime.now().plusHours(2L));
            }
        }

        if (locationDto != null) {
            event.setLocation(eventService.prepareLocation(locationDto));
        }

        if (paid != null) {
            event.setPaid(paid);
        }

        if (participantLimit != null) {
            event.setParticipantLimit(participantLimit);
        }

        if (requestModeration != null) {
            event.setRequestModeration(requestModeration);
        }

        if (title != null) {
            if (title.length() < 3 || title.length() > 120) {
                throw new IllegalArgumentException("Длина аннотации события не может быть меньше 20 и больше 7000" +
                        " символов");
            }
            event.setTitle(title);
        }
    }
}
