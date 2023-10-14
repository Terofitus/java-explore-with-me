package ru.practicum.controller;


import dto.EventFullDto;
import dto.EventShortDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.client.Client;
import ru.practicum.model.model_attribute.EventRequestParam;
import ru.practicum.service.event.EventService;
import ru.practicum.util.mapper.EventMapper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EventController {
    private final EventService eventService;
    private final Client statClient;
    private final EventMapper mapper;

    @GetMapping
    public List<EventShortDto> getEvents(@ModelAttribute EventRequestParam params, HttpServletRequest request) {
        statClient.addHit(request);
        return eventService.getEvents(params).stream().map(mapper::toShortDto).collect(Collectors.toList());
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable Integer eventId,  HttpServletRequest request) {
        statClient.addHit(request);
        return mapper.toDto(eventService.getEventById(eventId));
    }
}
