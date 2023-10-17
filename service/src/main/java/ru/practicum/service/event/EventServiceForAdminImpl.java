package ru.practicum.service.event;

import com.querydsl.core.types.Predicate;
import dto.ActionStateAdmin;
import dto.EventState;
import dto.LocationDto;
import dto.UpdateEventAdminRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictArgumentException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.model_attribute.AdminEventSearchParam;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.category.CategoryService;
import ru.practicum.util.PageableCreator;
import ru.practicum.util.QPredicates;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventServiceForAdminImpl implements EventServiceForAdmin {
    private final CategoryService categoryService;
    private final EventRepository eventRepository;
    private final EventService eventService;

    @Override
    public List<Event> eventSearch(AdminEventSearchParam params) {
        Predicate predicate = QPredicates.adminEventSearchPredicate(params);
        Pageable pageable = PageableCreator.toPageable(params.getFrom() == null ? 0 : params.getFrom(),
                params.getSize() == null ? 10 : params.getSize(), Sort.unsorted());
        Page<Event> eventsPage;
        if (predicate != null) {
            eventsPage = eventRepository.findAll(predicate, pageable);
        } else {
            eventsPage = eventRepository.findAll(pageable);
        }
        log.info("Запрошены события по параматрам:/n" + params);

        if (eventsPage.isEmpty()) {
            return Collections.emptyList();
        }
        eventService.addHitsToEvents(new ArrayList<>(eventsPage.toList()));
        return eventsPage.getContent();
    }

    @Transactional
    @Override
    public Event updateEvent(Integer eventId, UpdateEventAdminRequest requestBody) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            log.warn("Запрошено событие по несуществующему id={}", eventId);
            throw new NotFoundException("Не найдено событие с id=" + eventId);
        }
        Event event = eventOpt.get();
        prepareEventForUpdateAdmin(event, requestBody);
        Event event1 = eventRepository.save(event);
        log.info("Обновлено событие с id={}", eventId);
        eventService.addHitsToEvents(List.of(event1));
        return event1;
    }

    private void prepareEventForUpdateAdmin(Event event, UpdateEventAdminRequest request) {
        if (request == null) {
            event.setStateEnum(EventState.PUBLISHED);
            return;
        }

        fillingEventForUpdateAdmin(event, request);

        String stateAction = request.getStateAction();
        if (stateAction != null) {
            setEventStateForAdminUpdate(event, stateAction);
        }
    }

    private void fillingEventForUpdateAdmin(Event event, UpdateEventAdminRequest request) {
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
            event.setAnnotation(annotation);
        }

        if (categoryId != null) {
            Category category = categoryService.getCategoryById(categoryId, null);
            event.setCategory(category);
        }

        if (description != null) {
            event.setDescription(description);
        }

        if (eventDate != null) {
            if (eventDate.isAfter(LocalDateTime.now().plusHours(1L))) {
                event.setEventDate(eventDate);
            } else {
                throw new ConflictArgumentException("Дата и время проведения события должна быть не ранее чем " +
                        LocalDateTime.now().plusHours(1L));
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
            event.setTitle(title);
        }
    }

    private void setEventStateForAdminUpdate(Event event, String stateAction) {
        ActionStateAdmin action = ActionStateAdmin.forValues(stateAction);
        if (action == null) {
            throw new IllegalArgumentException("Администратор может назначить только эти статусы" +
                    " событиям: PUBLISH_EVENT, REJECT_EVENT");
        }
        switch (action) {
            case PUBLISH_EVENT:
                if (event.getStateEnum() == EventState.PENDING) {
                    event.setStateEnum(EventState.PUBLISHED);
                } else {
                    log.warn("Попытка изменения статуса события не ожидающего модерации");
                    throw new ConflictArgumentException("Опубликовать можно только событие ожидающее модерации");
                }
                break;
            case REJECT_EVENT:
                if (event.getStateEnum() == EventState.PUBLISHED) {
                    log.warn("Попытка отклонения публикации уже опубликованого события");
                    throw new ConflictArgumentException("Опубликовать можно только событие ожидающее модерации");
                } else {
                    event.setStateEnum(EventState.CANCELED);
                }
                break;
        }
    }
}
