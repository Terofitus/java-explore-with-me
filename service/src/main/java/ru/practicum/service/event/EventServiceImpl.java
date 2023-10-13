package ru.practicum.service.event;

import dto.ViewStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.client.Client;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.model.EventRequestParam;
import ru.practicum.repository.EventRepository;
import ru.practicum.util.PageableCreator;
import ru.practicum.util.event.EventRequestParamsPredicate;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final Client statClient;

    @Override
    public List<Event> getEvents(EventRequestParam params, HttpServletRequest request) {
        List<Event> events = (List<Event>) eventRepository.findAll(EventRequestParamsPredicate.getPredicate(params),
                PageableCreator.toPageable(params.getFrom(), params.getSize(), Sort.unsorted()));
        log.info("Запрошены события по параматрам:/n" + params);

        if (events.isEmpty()) {
            statClient.addHit(request);
            return Collections.emptyList();
        } else {
            String uri = request.getRequestURI();
            List<Integer> eventsId = events.stream().map(Event::getId).collect(Collectors.toList());
            List<String> uris = eventsId.stream().map(id -> uri + "/" + id).collect(Collectors.toList());
            List<ViewStats> views = statClient.gets(uris, false);

            Map<String,ViewStats> urisWithView = views.stream().
                    collect(Collectors.toMap(ViewStats::getUri, view -> view));
            events.forEach(event -> event.setViews(urisWithView.get(uri + "/" + event.getId()).getHits()));

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
            throw new NotFoundException("Не найдено ни 1 события с id=" + id);
        }
    }
}
