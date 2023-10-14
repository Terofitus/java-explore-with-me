package ru.practicum.service.event;

import dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.client.Client;
import ru.practicum.exception.ConflictArgumentException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.model_attribute.AdminEventSearchParam;
import ru.practicum.model.model_attribute.EventRequestParam;
import ru.practicum.repository.EventRepository;
import ru.practicum.service.category.CategoryService;
import ru.practicum.util.PageableCreator;
import ru.practicum.util.QPredicates;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryService categoryService;
    private final Client statClient;

    @Override
    public List<Event> getEvents(EventRequestParam params, HttpServletRequest request) {
        List<Event> events = (List<Event>) eventRepository.findAll(QPredicates.eventRequestParamPredicate(params),
                PageableCreator.toPageable(params.getFrom(), params.getSize(), Sort.unsorted()));
        log.info("Запрошены события по параматрам:/n" + params);

        if (events.isEmpty()) {
            statClient.addHit(request);
            return Collections.emptyList();
        } else {
            addHitsToEvents(events);
            if (params.getSort() != null) {
                switch(params.getSort()) {
                    case "EVENT_DATE":
                        events.sort(Comparator.comparing(Event::getEventDate));
                        break;
                    case "VIEWS":
                        events.sort(Comparator.comparing(Event::getViews));
                        break;
                }
            }

            statClient.addHit(request);

            return events;
        }
    }

    @Override
    public Event getEventById(Integer id, HttpServletRequest request) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        statClient.addHit(request);
        if (eventOpt.isPresent()) {
            log.info("Запрошено событие с id={}", id);
            return eventOpt.get();
        } else {
            log.warn("Запрошено событие по несуществующему id={}", id);
            throw new NotFoundException("Не найдено событие с id=" + id);
        }
    }

    @Override
    public void addHitsToEvents(List<Event> events) {
        List<Integer> eventsId = events.stream().map(Event::getId).collect(Collectors.toList());
        List<String> uris = eventsId.stream().map(id -> "/event/" + id).collect(Collectors.toList());

        List<ViewStats> views = statClient.gets(uris, false);
        Map<String,ViewStats> urisWithView = views.stream().
                collect(Collectors.toMap(ViewStats::getUri, view -> view));
        events.forEach(event -> event.setViews(urisWithView.get("/event/" + event.getId()).getHits()));
    }

    @Override
    public List<Event> eventSearch(AdminEventSearchParam params) {
        List<Event> events = (List<Event>) eventRepository.findAll(QPredicates.adminEventSearchPredicate(params),
                PageableCreator.toPageable(params.getFrom(), params.getSize(), Sort.unsorted()));
        log.info("Запрошены события по параматрам:/n" + params);

        if (events.isEmpty()) {
            return Collections.emptyList();
        } else {
            addHitsToEvents(events);
        }

        return events;
    }

    @Override
    public Event updateEvent(Integer eventId, UpdateEventAdminRequest requestBody) {
        Optional<Event> eventOpt = eventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            log.warn("Запрошено событие по несуществующему id={}", eventId);
            throw new NotFoundException("Не найдено событие с id=" + eventId);
        }
        Event event = eventOpt.get();
        prepareEventForUpdate(event, requestBody);
        Event event1 = eventRepository.save(event);
        log.info("Обновлено событие с id={}", eventId);
        addHitsToEvents(List.of(event1));
        return event1;
    }

    private void prepareEventForUpdate(Event event, UpdateEventAdminRequest request) {
        String annotation = request.getAnnotation();
        Integer categoryId = request.getCategory();
        String description = request.getDescription();
        LocalDateTime eventDate = request.getEventDate();
        LocationDto locationDto = request.getLocationDto();
        Boolean paid = request.getPaid();
        Integer participantLimit = request.getParticipantLimit();
        Boolean requestModeration = request.getRequestModeration();
        ActionStateAdmin stateAction = request.getStateAction();
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
            event.setLocation(new Location(null, locationDto.getLat(), locationDto.getLon()));
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

        if (stateAction != null) {
            switch (stateAction) {
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
                    }
            }
        }

        if (title != null) {
            event.setTitle(title);
        }

    }
}
