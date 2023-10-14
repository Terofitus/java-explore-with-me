package ru.practicum.controller;


import dto.EventFullDto;
import dto.EventShortDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.model_attribute.EventRequestParam;
import ru.practicum.service.event.EventService;
import ru.practicum.util.mapper.EventMapper;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class EventController {
    private final EventService eventService;
    private final EventMapper mapper;

    @GetMapping
    public List<EventShortDto> getEvents(@ModelAttribute EventRequestParam params, HttpServletRequest request) {
        return eventService.getEvents(params, request).stream().map(mapper::toShortDto).collect(Collectors.toList());
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventById(@PathVariable Integer eventId,  HttpServletRequest request) {
        return mapper.toDto(eventService.getEventById(eventId, request));
    }
}
