package ru.practicum.controller;

import dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.event.EventService;
import ru.practicum.service.request.RequestService;
import ru.practicum.util.mapper.EventMapper;
import ru.practicum.util.mapper.RequestMapper;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController {
    private final RequestService requestService;
    private final EventService eventService;
    private final EventMapper eventMapper;

    @GetMapping("/{userId}/requests")
    public List<ParticipationRequestDto> getRequestsByUserId(@PathVariable Integer userId) {
        return requestService.getRequestsByUserId(userId).stream()
                .map(RequestMapper::toDto).collect(Collectors.toList());
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable Integer userId,
                                              @RequestParam Integer eventId) {
        return RequestMapper.toDto(requestService.addRequest(userId, eventId));
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelRequest(@PathVariable Integer userId,
                                                 @PathVariable Integer requestId) {
        return RequestMapper.toDto(requestService.cancelRequest(userId, requestId));
    }

    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEventsOfOwner(@PathVariable Integer userId,
                                                @RequestParam(required = false, defaultValue = "0") Integer from,
                                                @RequestParam(required = false, defaultValue = "10") Integer size) {
        return eventService.getEventsOfOwner(userId, from, size)
                .stream().map(eventMapper::toShortDto).collect(Collectors.toList());
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@Valid @RequestBody NewEventDto dto ,@PathVariable Integer userId) {
        return eventMapper.toDto(eventService.addEvent(userId, dto));
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventFullDto getEventForOwner(@PathVariable Integer userId, @PathVariable Integer eventId) {
        return eventMapper.toDto(eventService.getEventForOwner(userId, eventId));
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventFullDto updateEvent(@Valid @RequestBody(required = false) UpdateEventUserRequest dto,
                                    @PathVariable Integer userId, @PathVariable Integer eventId) {
        return eventMapper.toDto(eventService.updateEvent(userId, eventId, dto));
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsForOwnerOfEvent(@PathVariable Integer userId,
                                                              @PathVariable Integer eventId) {
        return requestService.getRequestsByOwnerId(userId, eventId).stream().map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateEventRequests(@PathVariable Integer userId,
                                                             @PathVariable Integer eventId,
                                                             @Valid @RequestBody(required = false)
                                                                  EventRequestStatusUpdateRequest dto) {
        return requestService.updateEventRequests(userId, eventId, dto);
    }
}
