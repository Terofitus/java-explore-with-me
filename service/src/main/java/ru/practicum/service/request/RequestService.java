package ru.practicum.service.request;

import dto.EventRequestStatusUpdateRequest;
import dto.EventRequestStatusUpdateResult;
import ru.practicum.model.Request;

import java.util.List;

public interface RequestService {
    List<Request> getRequestsByUserId(Integer id);

    Request addRequest(Integer userId, Integer eventId);

    Request cancelRequest(Integer userId, Integer requestId);

    List<Request> getRequestsByOwnerId(Integer userId, Integer requestId);

    EventRequestStatusUpdateResult updateEventRequests(Integer userId, Integer eventId,
                                                       EventRequestStatusUpdateRequest dto);
}
