package ru.practicum.service.event;

import com.querydsl.core.types.Predicate;
import dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.Client;
import ru.practicum.exception.ConflictArgumentException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Category;
import ru.practicum.model.Event;
import ru.practicum.model.Location;
import ru.practicum.model.User;
import ru.practicum.model.model_attribute.AdminEventSearchParam;
import ru.practicum.model.model_attribute.EventRequestParam;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.LocationRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.service.category.CategoryService;
import ru.practicum.util.PageableCreator;
import ru.practicum.util.QPredicates;
import ru.practicum.util.mapper.EventMapper;
import ru.practicum.util.mapper.LocationMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventServiceImpl implements EventService {
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final Client statClient;

    @Transactional
    @Override
    public List<Event> getEvents(EventRequestParam params) {
        List<Event> events = eventRepository.findAll(QPredicates.eventRequestParamPredicate(params),
                PageableCreator.toPageable(params.getFrom() == null ? 0 : params.getFrom(),
                        params.getSize() == null ? 20 : params.getSize(), Sort.unsorted())).toList();
        log.info("Запрошены события по параматрам: " + params);

        if (events.isEmpty()) {
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

            return events;
        }
    }

    @Transactional
    @Override
    public Event getEventById(Integer id) {
        Optional<Event> eventOpt = eventRepository.findById(id);
        if (eventOpt.isPresent()) {
            log.info("Запрошено событие с id={}", id);
            Event event = eventOpt.get();
            List<Event> events = new ArrayList<>();
            events.add(event);
            addHitsToEvents(events);
            return event;
        } else {
            log.warn("Запрошено событие по несуществующему id={}", id);
            throw new NotFoundException("Не найдено событие с id=" + id);
        }
    }

    @Override
    public void addHitsToEvents(List<Event> events) {
        if (events == null || events.isEmpty()) return;
        List<Integer> eventsId = events.stream().map(Event::getId).collect(Collectors.toList());
        List<String> uris = eventsId.stream().map(id -> "/event/" + id).collect(Collectors.toList());

        List<ViewStats> views = statClient.gets(uris, false);
        Map<String,ViewStats> urisWithView = views.stream().
                collect(Collectors.toMap(ViewStats::getUri, view -> view));
        events.forEach(event -> event.setViews(urisWithView.get("/event/" + event.getId()) == null ? 0 :
                urisWithView.get("/event/" + event.getId()).getHits()));
    }

    @Override
    public List<Event> eventSearch(AdminEventSearchParam params) {
        Predicate predicate = QPredicates.adminEventSearchPredicate(params);
        Pageable pageable = PageableCreator.toPageable(params.getFrom() == null ? 0 : params.getFrom(),
                params.getSize() == null ? 20 : params.getSize(), Sort.unsorted());
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
        addHitsToEvents(eventsPage.toList());
        return eventsPage.toList();
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
        addHitsToEvents(List.of(event1));
        return event1;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void prepareEventForUpdateAdmin(Event event, UpdateEventAdminRequest request) {
        if (request == null) {
            event.setStateEnum(EventState.PUBLISHED);
            return;
        }
        String annotation = request.getAnnotation();
        Integer categoryId = request.getCategory();
        String description = request.getDescription();
        LocalDateTime eventDate = request.getEventDate();
        LocationDto locationDto = request.getLocation();
        Boolean paid = request.getPaid();
        Integer participantLimit = request.getParticipantLimit();
        Boolean requestModeration = request.getRequestModeration();
        String stateAction = request.getStateAction();
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
            ActionStateAdmin action = ActionStateAdmin.forValues(stateAction);
            if (action == null) throw new IllegalArgumentException("Администратор может назначить только эти статусы" +
                    " событиям: PUBLISH_EVENT, REJECT_EVENT");
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

        if (title != null) {
            event.setTitle(title);
        }
    }

    @Override
    public List<Event> getEventsOfOwner(Integer userId, Integer from, Integer size) {
        Pageable pageable = PageableCreator.toPageable(from, size, null);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        log.info("Запрошены события добавленные пользователем с id={}", userId);
        addHitsToEvents(events);
        return events;
    }

    @Transactional
    @Override
    public Event addEvent(Integer userId, NewEventDto dto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id=%s не найден", userId)));
        Category category = categoryRepository.findById(dto.getCategory()).orElseThrow(
                () -> new NotFoundException(String.format("Категория с id=%s не найдена", dto.getCategory())));
        Location location = prepareLocation(dto.getLocation());
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
        addHitsToEvents(events);
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
        String annotation = request.getAnnotation();
        Integer categoryId = request.getCategory();
        String description = request.getDescription();
        LocalDateTime eventDate = request.getEventDate();
        LocationDto locationDto = request.getLocation();
        Boolean paid = request.getPaid();
        Integer participantLimit = request.getParticipantLimit();
        Boolean requestModeration = request.getRequestModeration();
        String stateAction = request.getStateAction();
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
            ActionStateUser action = ActionStateUser.forValues(stateAction);
            if (action == null) throw new IllegalArgumentException("Пользователь может назначить только эти статусы" +
                    " своим событиям: SEND_TO_REVIEW, CANCEL_REVIEW");
            switch (action) {
                case SEND_TO_REVIEW:
                    event.setStateEnum(EventState.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setStateEnum(EventState.CANCELED);
            }
        }

        if (title != null) {
            if (title.length() < 3 || title.length() > 120) {
                throw new IllegalArgumentException("Длина аннотации события не может быть меньше 20 и больше 7000" +
                        " символов");
            }
            event.setTitle(title);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Location prepareLocation(LocationDto loc) {
            Location location = locationRepository.findByLatAndLon(loc.getLat(), loc.getLon());
            if (location == null) {
                location = locationRepository.save(LocationMapper.toLocation(loc));
            }
            return location;
    }
}
