package ru.practicum.service.request;

import dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.ConflictArgumentException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.model.Event;
import ru.practicum.model.QRequest;
import ru.practicum.model.Request;
import ru.practicum.model.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;
import ru.practicum.util.mapper.RequestMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public List<Request> getRequestsByUserId(Integer id) {
        if (userRepository.existsById(id)) {
            List<Request> requests = (List<Request>) requestRepository.findAll(QRequest.request.requester.id.eq(id));
            log.info("Запрошены заявки пользователя с id={}", id);
            return requests;
        } else {
            log.warn("Попытка получения заявок несуществующего пользователя с id={}", id);
            throw new NotFoundException(String.format("Пользователь с id=%s не найден", id));
        }
    }

    @Transactional
    @Override
    public Request addRequest(Integer userId, Integer eventId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("Пользователь с id=%s не найден", userId)));
        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие с id=" + eventId + " не существует"));

        if (event.getStateEnum() != EventState.PUBLISHED) {
            log.warn("Попытка добавления заявки на участие в неопубликованном событии с id={}", eventId);
            throw new ConflictArgumentException("Нельзя создать заявку на участие в неопубликованном событии");
        }

        if (requestRepository.existsRequestByRequesterIdAndEventId(userId, eventId)) {
            log.warn("Попытка добавления уже существующей заявки на участие");
            throw new ConflictArgumentException(String.format("Заявка на участие в событии с id=%d от пользователя " +
                    "с id=%d уже добавлена", eventId, userId));
        }

        if (user.getId().equals(event.getInitiator().getId())) {
            log.warn("Попытка добавления завяки на участие в событии с id={} от инициатора этого события", eventId);
            throw new ConflictArgumentException("Невозможно создать заявку на участие в событии от инициатора этого" +
                    "события");
        }

        if (event.getParticipantLimit() != 0 && (event.getParticipants().size() >= event.getParticipantLimit())) {
            log.warn("Попытка добавления заявки на участие в событии с id={} с превышением лимита участников", eventId);
            throw new ConflictArgumentException("Достигнут лимит участников события с id=" + eventId);
        }
        Request request = new Request(null, LocalDateTime.now(), event, user, EventRequestStatus.PENDING);
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(EventRequestStatus.CONFIRMED);
        }

        Request updateRequest = requestRepository.save(request);
        log.info("Добавлена заявка на участие в событии с id={} от пользователя с id={}", eventId, userId);
        return updateRequest;
    }

    @Transactional
    @Override
    public Request cancelRequest(Integer userId, Integer requestId) {
        Request request = requestRepository.findById(requestId).orElseThrow(() ->
                new NotFoundException("Нет заявки на участие с id=" + requestId));

        if (!Objects.equals(request.getRequester().getId(), userId)) {
            log.warn("Попытка отмены заявки на участие пользователем с id={}, не являющимся ее владельцем", userId);
            throw new ConflictArgumentException("Отменить заявку на участие может только ее владелец");
        }

        request.setStatus(EventRequestStatus.CANCELED);
        Request updatedRequest = requestRepository.save(request);
        log.info("Отменена заявка на участие с id={}", requestId);
        return updatedRequest;
    }

    @Override
    public List<Request> getRequestsByOwnerId(Integer userId, Integer eventId) {
        return requestRepository.findAllRequestsForOwnerOfEvent(eventId, userId);
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateEventRequests(Integer userId, Integer eventId,
                                                              EventRequestStatusUpdateRequest dto) {
        if (!requestRepository.existsRequestByEventIdAndEventInitiatorId(eventId, userId)) {
            log.warn("Попытка изменения статуса заявок на участие в событии с id={} пользователем с id={}",
                    eventId, userId);
            throw new NotFoundException("Нет заявок на участие в событии");
        }

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotFoundException("Событие с id=" + eventId + " не существует"));


        if (dto == null) {
            throw new ConflictArgumentException("Отсутствует тело запроса");
        }
        List<Request> requestList = requestRepository.findAllById(dto.getRequestIds());
        if(requestList.isEmpty()) {
            throw new NotFoundException("Не найдено ни 1 заявки по переданным id=" + dto.getRequestIds());
        }

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            requestList.forEach(request -> request.setStatus(EventRequestStatus.CONFIRMED));
            requestRepository.saveAll(requestList);
            Set<ParticipationRequestDto> requestsDto = requestList.stream().map(RequestMapper::toDto).
                    collect(Collectors.toSet());
            return new EventRequestStatusUpdateResult(requestsDto, Collections.emptySet());
        }

        if (requestList.stream().anyMatch(request -> request.getStatus() != EventRequestStatus.PENDING)) {
            log.warn("Попытка изменения статуса у заявок на участие в событии с id={} не находящихся " +
                    "в состоянии ожидания подтверждения", eventId);
            throw new ConflictArgumentException("Нельзя изменить статус у заявки ненахоядщейся в состоянии" +
                    " ожидания подтверждения");
        }

        switch (dto.getStatus()) {
            case CONFIRMED:
                if (event.getParticipantLimit() > 0 && event.getParticipantLimit() == event.getParticipants().size()) {
                    log.warn("Попытка подтверждения заявки на участие в событии с id={} при " +
                            " достижении лимита на количество участников", eventId);
                    throw new ConflictArgumentException("Заявка на участие в событии с id=" + eventId + " не может " +
                            "быть подтверждена, так как достигло лимита на количество участников");
                }
                if (((event.getParticipantLimit() - event.getParticipants().size()) < requestList.size())) {
                    Map<String,List<Request>> map = updateRequestStatus(event, requestList);
                    List<Request> list = new ArrayList<>();
                    list.addAll(map.get("confirmed"));
                    list.addAll(map.get("rejected"));
                    requestRepository.saveAll(list);
                    log.info("Обновлены статусы заявок на участие в событии с id={}", eventId);
                    return new EventRequestStatusUpdateResult(map.get("confirmed")
                            .stream().map(RequestMapper::toDto).collect(Collectors.toSet()),
                            map.get("rejected")
                                    .stream().map(RequestMapper::toDto).collect(Collectors.toSet()));
                } else {
                    requestList.forEach(request -> request.setStatus(EventRequestStatus.CONFIRMED));
                    requestRepository.saveAll(requestList);
                    log.info("Обновлены статусы заявок на участие в событии с id={}", eventId);
                    Set<ParticipationRequestDto> requestsDto = requestList.stream().map(RequestMapper::toDto).
                            collect(Collectors.toSet());
                    return new EventRequestStatusUpdateResult(requestsDto, Collections.emptySet());
                }
            case REJECTED:
                requestList.forEach(request -> request.setStatus(EventRequestStatus.REJECTED));
                requestRepository.saveAll(requestList);
                log.info("Обновлены статусы заявок на участие в событии с id={}", eventId);
                Set<ParticipationRequestDto> requestsDto = requestList.stream().map(RequestMapper::toDto).
                        collect(Collectors.toSet());
                return new EventRequestStatusUpdateResult(Collections.emptySet(), requestsDto);
            default:
                throw new ConflictArgumentException("Неизвестный статус:" + dto.getStatus());
        }


    }

    private Map<String,List<Request>> updateRequestStatus(Event event, List<Request> requests) {
        Map<String,List<Request>> setRequests = new HashMap<>();
        int remainder = event.getParticipantLimit() - event.getParticipants().size();
        List<Request> list = new ArrayList<>(requests);
        List<Request> confirmed = list.subList(0, remainder);
        confirmed.forEach(request -> request.setStatus(EventRequestStatus.CONFIRMED));
        List<Request> rejected = list.subList(remainder, list.size());
        rejected.forEach(request -> request.setStatus(EventRequestStatus.REJECTED));
        setRequests.put("confirmed", confirmed);
        setRequests.put("rejected", rejected);
        return setRequests;
    }
}
