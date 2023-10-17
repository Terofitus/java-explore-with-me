package ru.practicum.service.event;

import dto.EventState;
import dto.LocationDto;
import dto.ViewStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.Client;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.model.EventSort;
import ru.practicum.model.Location;
import ru.practicum.model.model_attribute.EventRequestParam;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.LocationRepository;
import ru.practicum.util.PageableCreator;
import ru.practicum.util.QPredicates;
import ru.practicum.util.mapper.LocationMapper;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final LocationRepository locationRepository;
    private final Client statClient;

    @Transactional
    @Override
    public List<Event> getEvents(EventRequestParam params) {
        List<Event> events = new ArrayList<>(eventRepository.findAll(QPredicates.eventRequestParamPredicate(params),
                PageableCreator.toPageable(params.getFrom() == null ? 0 : params.getFrom(),
                        params.getSize() == null ? 10 : params.getSize(), Sort.unsorted())).toList());
        log.info("Запрошены события по параматрам: " + params);

        if (events.isEmpty()) {
            return Collections.emptyList();
        } else {
            addHitsToEvents(events);
            if (params.getSort() != null) {
                EventSort sort = params.getSort();
                if (sort == EventSort.EVENT_DATE) {
                    events.sort(Comparator.comparing(Event::getEventDate));
                } else {
                    events.sort(Comparator.comparing(Event::getViews));
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
            Event event = eventOpt.get();
            if (event.getStateEnum() != EventState.PUBLISHED) {
                log.warn("Запрошено событие по с id={}, находящееся не в состоянии публикации", id);
                throw new NotFoundException("Не найдено событие с id=" + id);
            }
            List<Event> events = new ArrayList<>();
            events.add(event);
            addHitsToEvents(events);
            log.info("Запрошено событие с id={}", id);
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
        Map<String, ViewStats> urisWithView = views.stream()
                .collect(Collectors.toMap(ViewStats::getUri, view -> view));
        events.forEach(event -> event.setViews(urisWithView.get("/event/" + event.getId()) == null ? 0 :
                urisWithView.get("/event/" + event.getId()).getHits()));
    }


    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Location prepareLocation(LocationDto loc) {
        Location location = locationRepository.findByLatAndLon(loc.getLat(), loc.getLon());
        if (location == null) {
            location = locationRepository.save(LocationMapper.toLocation(loc));
        }
        return location;
    }
}
